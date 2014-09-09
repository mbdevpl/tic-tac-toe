package mb.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import mb.tictactoe.ServerFrame;
import mb.tictactoe.TicTacToeConstants;
import mb.tictactoe.Ttt;
import mb.tictactoe.data.Board;
import mb.tictactoe.data.CellData;
import mb.tictactoe.data.CellState;
import mb.tictactoe.data.ChatMsg;
import mb.tictactoe.data.Game;
import mb.tictactoe.data.Participant;
import mb.tictactoe.data.ServerData;
import mb.tictactoe.data.SystemMsg;

/**
 * Server for TicTacToe game. Establishes serversocket, launches frame, and listens
 * for incoming connections. In case of a connection, new WebThreadServerClient object is created.
 * @author mb
 */
public class WebThreadServer extends WebThread {
	
	private ServerSocket serverSocket = null;
	private volatile ArrayList<WebThreadServerClient> clients = null;
	public volatile Game game;
	private String status;
	private ServerData data;
	private StreamPipe pipe;
	private ServerFrame frame;
	
	/**
	 * Unique number that is incremented after every accept() success, but never decremented.
	 */
	private int nextClientId = 0;
	
	/**
	 * Constructs new server, with specific parameters of the game board.
	 * @param host
	 * @param port
	 * @param timeout
	 * @param boardWidth
	 * @param boardHeight
	 * @param winningLen
	 * @throws IOException if the ServerSocket cannot be constructed
	 */
	public WebThreadServer(String host, int port, int timeout, int boardWidth, int boardHeight, int winningLen) throws IOException {
		super("server", host, port, timeout);
		this.setWebId(port);
	    serverSocket = new ServerSocket();
	    serverSocket.setSoTimeout(timeout);
	    serverSocket.bind(new InetSocketAddress(host, port));
	    clients = new ArrayList<WebThreadServerClient>();
	    boolean created = false,loaded = false;
	    try {
	    	data = new ServerData(XmlTree.createFromFile(TicTacToeConstants.SERVER_DATABASE_PATH));
	    	loaded = true;
	    } catch (FileNotFoundException e) {
	    	//create new file if there is no database
	    	data = new ServerData();
	    	data.addAccount("admin", "asjgrh13252321tadgGWSGRauwvn23452345rtiausdEWMEQPzixgeyrg23019480gsf", 0, 0);
	    	data.toXmlTree().saveToFile(TicTacToeConstants.SERVER_DATABASE_PATH);
	    	created = true;
	    }
	    game = new Game("server at " + host, new Board(boardWidth, boardHeight, winningLen));
	    status = "Server was just created.";
	    pipe = new StreamPipe();
	    frame = new ServerFrame(this);
	    
	    frame.launch();
	    log("Server was constructed on port: " + serverSocket.getLocalPort() + ".");
	    if(loaded)
	    	log("Server data loaded.");
	    if(created)
	    	log("New server database file created (" + TicTacToeConstants.SERVER_DATABASE_PATH + ").");
	}
	
	/**
	 * Checks if this server contains a specified client.
	 * @param client to be checked
	 * @return true if there is such client
	 */
	synchronized public boolean contains(WebThreadClient client) {
		synchronized(clients) {
			return clients.contains(client);
		}
	}
	
	/**
	 * Removes client from clients list.
	 * @param client the client
	 * @return true if removal was successful
	 */
	synchronized public boolean remove(WebThreadClient client) {
		game.removeParticipant(client.getWebId());
		boolean removed = clients.remove(client);
		if(removed) log("A client was removed, remaining clients: " + size() + ".");
		else log("There was a request to remove a client that is not connected.");
		return removed;
	}
	
	/**
	 * 
	 * @return number of connected clients
	 */
	synchronized private int size() {
		synchronized(clients) {
			return clients.size();
		}
	}
	
	/**
	 * Returns unique id number, needed by clients.
	 * @return integer, incremented with each execution by one
	 */
	synchronized public int getNextClientId() {
		synchronized(clients) {
			return nextClientId++;
		}
	}
	
	/**
	 * Closes the ServerSocket, and stops the thread.
	 */
	public void close() {
		state = WAITING;
		try {
			serverSocket.close();
		} catch (IOException e) {
			//some error, irrelevant
		}
		stopMe();
	}
	
	/**
	 * Disconnects a specific client.
	 * @param clientId
	 */
	public void disconnect(int clientId) {
		for(WebThreadClient c: clients)
			if(c.getWebId() == clientId) {
				game.removeParticipant(clientId);
				game.refreshRemoteData(this);
				c.disconnect();
				return;
			}
		throw new NullPointerException("could not disconnect a client that was not connected in the first place");
	}
	
	/**
	 * Disconnects all clients, and removes them from the clients list.
	 */
	public void disconnectAll() {
		log("Disconnecting all clients...");
		for(int i = clients.size() - 1; i >= 0; i--) {
			WebThreadServerClient c = clients.get(i);
			game.removeParticipant(c.getWebId());
			game.refreshRemoteData(this);
			c.disconnect();
			remove(c);
		}
	}
	
	/**
	 * Sends a ping question to a specified client.
	 * @param clientId id of client with which the connection is to be checked
	 */
	public void ping(int clientId) {
		for(WebThreadClient c: clients)
			if(c.getWebId() == clientId) {
				c.ping();
				break;
			}
	}
	
	/**
	 * Pings all clients.
	 */
	public void pingAll() {
		for(WebThreadServerClient c: clients)
			c.ping();
	}
	
	@Override
	public void log(String s) {
		frame.log(s);
	}
	
	/**
	 * Sends an object o to client c. 
	 * @param c
	 * @param o
	 */
	public void sendTo(WebThreadClient c, Xmlable o) {
		c.send(o);
	}
	
	/**
	 * Sends object o to all connected clients.
	 * @param o
	 */
	public void sendToAll(Xmlable o) {
		//String xmlString = XmlTree.createXmlString(o);
		if(o instanceof ChatMsg)
			frame.getLastChat().addEntry(o.toString());
		if(o instanceof SystemMsg) {
			String str = ((SystemMsg)o).getMsg();
			if(str.startsWith("status="))
				this.status = str.substring(7);
		}
		log("sending " + o.getClass().getSimpleName() + " to every client.");
		for(WebThreadServerClient c: clients)
			c.send(o);
	}
	
	/**
	 * Sends object o to all connected clients, with exception for the excluded one.
	 * @param excluded the excluded client
	 * @param o
	 */
	public void sendToAllExcept(WebThreadClient excluded, Xmlable o) {
		//String xmlString = XmlTree.createXmlString(o);
		log("sending new " + o.getClass().getSimpleName());
		for(WebThreadServerClient c: clients)
			if(!c.equals(excluded)) c.send(o);
	}
	
	@Override
	protected void doActions() {
		try {
			//maintenance
			//synchronized(game) {
			//	if(game.syncParticipantList(clients))
			//		log("Some participants were removed from game.");
			//}
			
			Socket clientSocket = new Socket();
			clientSocket.setSoTimeout(WebThreadServerClient.defaultTimeout);
			clientSocket = serverSocket.accept();
			
			//new client procedure:
			log("A new client has connected. Total " + (size() + 1) + " clients.");
			clientSocket.setSoTimeout(WebThreadServerClient.defaultTimeout);
			WebThreadServerClient newClient = new WebThreadServerClient(this, pipe.getStreamSideB(), clientSocket);
			clients.add(newClient);
			newClient.start();
			newClient.send(new SystemMsg( "webid=" + String.valueOf( newClient.getWebId() ) ));
			newClient.send(new SystemMsg( "status=" + this.status ));
			
			timeoutCounter = 0;
		} catch (SocketTimeoutException e) {
			if(clients.size() == 0) {
				timeoutCounter++;
				if(timeoutCounter*1000 == (getTimeout()*60)/1000)
					log("Waiting for clients (" + timeoutCounter/60 + ")...");
				if(timeoutCounter >= (getTimeout()*3600)/1000) {
					//server will stop after 60 minutes without any connections
					// if there are no active connections
					log("60 minutes without connections.");
					stopMe();
				}
			} else {
				//if(counter % 60 == 0)
					//log("Currently connected clients: " + size() + ".");
			}
		} catch (IOException e) {
		    log("Failed to accept on port " + serverSocket.getLocalPort() + ".");
		}
		//counter++;
		//if(counter == 60) counter = 0;
	}
	
	/**
	 * Tries to log in a new participant of game.
	 * @param p username, password etc.
	 * @return a message describing the result of the login trial
	 */
	public String tryToLogin(Participant p) {
		if(!data.containsAccount(p))
			return "nosuchaccount";
		if(!data.credentialsOk(p.getName(), p.getPassword()))
			return "badpassword";
		if(game.getPlayer(1) != null && game.getPlayer(2) != null && p.isPlayer())
			return "toomanyplayers";
		if(!game.addParticipant(p))
			return "alreadyindb";
		
		return "loggedin";
	}

	/**
	 * Tries to create an account for a new user.
	 * @param p user for who the account will be created
	 * @return true if account was created, false if server was unable to pull it off
	 */
	public boolean tryToCreateAccount(Participant p) {
		if(data.addAccount(p.getName(), p.getPassword(), 0, 0)) {
			data.toXmlTree().saveToFile(TicTacToeConstants.SERVER_DATABASE_PATH);
			return true;
		}
		return false;
	}
	
	public WebThreadServerClient getFirstPlayer() {
		Participant p1 = game.getPlayer(1);
		for(WebThreadServerClient c: clients)
			if(c.getParticipantName().equals(p1.getName()))
				return c;
		return null;
	}
	
	public WebThreadServerClient getSecondPlayer() {
		Participant p2 = game.getPlayer(2);
		for(WebThreadServerClient c: clients)
			if(c.getParticipantName().equals(p2.getName()))
				return c;
		return null;
	}
	
	/**
	 * Checks winning/loosing conditions and in case if any is fulfilled, sends appropiate messages. 
	 * @return if game ended just now, because of the conditions
	 */
	public boolean gameEnds() {
		Board b = game.getBoard();
		if(Ttt.gameOver(b)) {
			sendToAll(new SystemMsg("notplayable"));
			CellState winner = new CellState(Ttt.winner(b));
			
			Participant p1 = game.getPlayer(1), p2 = game.getPlayer(2);

			for(WebThreadServerClient c: clients)
				if(c.getParticipantName().equals(p1.getName())) {
					if(winner.isCross()) {
						sendToAll(new SystemMsg("status=Game over! " + c.getParticipantName() + " won!"));
						sendTo(c, new SystemMsg("youwon"));
					} else if(winner.isNought()) {
						sendTo(c, new SystemMsg("youlost"));
					} else {
						sendTo(c, new SystemMsg("draw"));
					}
				} else if(c.getParticipantName().equals(p2.getName())) {
					if(winner.isCross()) {
						sendTo(c, new SystemMsg("youlost"));
					} else if(winner.isNought()) {
						sendToAll(new SystemMsg("status=Game over! " + c.getParticipantName() + " won!"));
						sendTo(c, new SystemMsg("youwon"));
					} else {
						sendToAll(new SystemMsg("status=Game over! It is a draw!"));
						sendTo(c, new SystemMsg("draw"));
					}
					break;
				}
			
			return true;
		}
		return false;
	}
	
	public void increaseWinCount(Participant p) {
		if(data.containsAccount(p)) {
			data.increaseGamesWon(p);
			data.increaseGamesPlayed(p);
			data.toXmlTree().saveToFile(TicTacToeConstants.SERVER_DATABASE_PATH);
		}
	}

	public void increasePlayedCount(Participant p) {
		if(data.containsAccount(p)) {
			data.increaseGamesPlayed(p);
			data.toXmlTree().saveToFile(TicTacToeConstants.SERVER_DATABASE_PATH);
		}
		
	}
	
	public int getWebIdFor(Participant p) {
		for(WebThreadServerClient c: clients)
			if(c.getParticipantName().equals(p.getName()))
				return c.getWebId();
		throw new NullPointerException("cannot get web ID for participant that is not logged in");
	}
	
	public void receivedObject(WebThreadServerClient receiver, Xmlable o) {
		if(o instanceof SystemMsg) {
			String sm = ((SystemMsg)o).getMsg();
			if(sm.equals("onemoregame")) {
				synchronized(game) {
					if(game.isOver()) {
						game.reInit();
						sendToAll(game);
						sendToAll(new SystemMsg("playable"));
						sendTo(getFirstPlayer(), new SystemMsg("turnstart"));
						sendToAll(new SystemMsg("status=Next duel starts! It is " + game.getCurrPlayer().getName()
								+ "'s (playing '" + game.getCurrPlayer().getSymbol() + "') turn now."));
					}
				}
			} else if(sm.equals("turnend")) {
				if(!gameEnds()) {
					sendToAllExcept(receiver, new SystemMsg("turnstart"));
					sendToAll(new SystemMsg("status=It is " + game.getCurrPlayer().getName()
							+ "'s (playing '" + game.getCurrPlayer().getSymbol() + "') turn now."));
				} else
					sendToAll(new SystemMsg("duelended"));

			} else if(sm.equals("getCounts")) {
				receiver.send(new SystemMsg("wins=" + data.getWinCount(receiver.participant)));
				receiver.send(new SystemMsg("play=" + data.getPlayCount(receiver.participant)));
				
			} else if(sm.equals("incWin")) {
				increaseWinCount(receiver.participant);

			} else if(sm.equals("incPlayed")) {
				increasePlayedCount(receiver.participant);
				
			} else if(sm.equals("permissionToCreate")) {
				if(tryToCreateAccount(receiver.participant))
					sendTo(receiver, new SystemMsg("accountcreated"));
			} else if(sm.equals("deleteme")) {
				
				if(data.removeAccount(receiver.participant)) {
					game.removeParticipant(receiver.getWebId());
					data.toXmlTree().saveToFile(TicTacToeConstants.SERVER_DATABASE_PATH);
					receiver.disconnect();
				} else
					receiver.send(new SystemMsg("badpassword"));
					
			}
		} else if(o instanceof CellData) {
			//log("received CellDAAAAATAAAAA!!");
			CellData cd = ((CellData)o);
			if(game.addMove(cd))
				sendToAll(game);
			//log("YO.");
			
		} else if(o instanceof ChatMsg) {
			game.addChatEntry((ChatMsg)o);
			sendToAll(o);
		} else if(o instanceof Participant) {
			receiver.participant = (Participant)o;
			//receiver.participant.setTempId(receiver.getWebId());
			String s = tryToLogin(receiver.participant);

			if(s.equals("loggedin")) {
				receiver.participant.setTempId(receiver.getWebId());
				sendToAll( new ChatMsg(getWebId(), "SERVER", "system", receiver.participant.getName() + " joined the game.") );
			}
			
			sendTo(receiver, new SystemMsg(s)); 
			
			if(s.equals("loggedin")) {
				sendTo(receiver, new SystemMsg("side=" + receiver.participant.getSide()));
				if(receiver.participant.getSide() == 1) {
					sendTo(receiver, new SystemMsg("turnstart"));
					sendToAll(new SystemMsg("status=Waiting for more players."));
					
				} else if(receiver.participant.getSide() == 2) {
					sendToAll(new SystemMsg("playable"));
					if(this.getFirstPlayer().participant.getName().equals(game.getCurrPlayer().getName()))
						sendTo(this.getFirstPlayer(), new SystemMsg("turnstart"));
					else
						sendTo(this.getSecondPlayer(), new SystemMsg("turnstart"));
					sendToAll(new SystemMsg("status=Game started, it is " + game.getCurrPlayer().getName() + "'s turn now."));
				}
				sendToAll(game);
			}
		}
	}
}
