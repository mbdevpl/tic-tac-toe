package mb.web;

import java.io.IOException;
import java.net.Socket;

import mb.tictactoe.ClientFrame;
import mb.tictactoe.data.ChatMsg;
import mb.tictactoe.data.Game;
import mb.tictactoe.data.Participant;
import mb.tictactoe.data.PlayerSide;
import mb.tictactoe.data.SystemMsg;

/**
 * Client-side of the Tic Tac Toe application. Using given coordinates and credentials, it tries to connect
 * and log in to server. If the credentials are incorrect, it exits immediately.  
 * @author mb
 */
public class WebThreadRemoteClient extends WebThreadClient {
	/**
	 * Frame used by the user to play the game.
	 */
	private ClientFrame frame;
	
	/**
	 * User name & password.
	 */
	public volatile Participant participant;
	
	/**
	 * Was the permission to create account if it does not exists given?
	 */
	private boolean permissionToCreate;
	
	public volatile String playCount, winCount;
	
	/**
	 * Constructor.
	 * @param socket
	 * @param participant
	 * @param createNew permission to create new account
	 * @throws IOException
	 */
	public WebThreadRemoteClient(Socket socket, Participant participant, boolean createNew) throws IOException {
		super("remote", socket);
		setWebId(socket.getLocalPort());
	    this.participant = participant;
	    this.permissionToCreate = createNew;
		this.frame = new ClientFrame(this);

		frame.launch();
		log("Remote client connected by port " + socket.getLocalPort() + " to server "
				+ socket.getLocalAddress().getHostName() + ":" + socket.getPort() + ".");
		
		//initial communication
		send(participant);
	}
	
	/*public WebThreadRemoteClient(String hostname, int port, int timeout) throws IOException {
		super("remote", hostname, port, timeout);
		setWebId(socket.getLocalPort());
	    //frame = new ClientFrame(this);
	    
		//frame.launch();
		log("BAD remote client connected by port " + socket.getLocalPort() + " to server "
				+ socket.getLocalAddress().getHostName() + ":" + socket.getPort() + ".");
	}*/
	
	public String toString() {
		if(participant != null)
			return "remoteClient(" + this.getWebId() + "," + this.participant.getName() + ")";
		else
			return "remoteClient(" + this.getWebId() + ",)";
	}
	
	@Override
	protected void receivedAnotherObject(Xmlable o) {
		if(o instanceof SystemMsg) {
			String sm = ((SystemMsg)o).getMsg();
			if(sm.equals("youwon")) {
				frame.launchInfoDialog("Game over", "Congratulations, you won! Your server records were updated.");
				send(new SystemMsg("incWin"));
				
			} else if(sm.equals("youlost")) {
				frame.launchInfoDialog("Game over", "What a pity, you lost! Your server records were updated.");
				send(new SystemMsg("incPlayed"));
				
			} else if(sm.equals("draw")) {
				frame.launchInfoDialog("Game over", "It is a draw! Your server records were updated.");
				send(new SystemMsg("incPlayed"));
				
			} else if(sm.equals("duelended")) {
				if(participant.isPlayer())
					frame.oneMore.setEnabled(true);
				
			} else if(sm.equals("turnstart")) {
				frame.oneMore.setEnabled(false);
				if(participant.isPlayer())
					frame.movesActive = true;
				
			} else if(sm.equals("turnend"))
				frame.movesActive = false;
			
			else if(sm.equals("playable"))
				frame.playable = true;
			
			else if(sm.equals("notplayable"))
				frame.playable = false;
			
			else if(sm.startsWith("status=")) {
				frame.setStatus(sm.substring(7));
				
			}else if(sm.startsWith("webid=")) {
				Integer i = Integer.valueOf(sm.substring(6));
				this.setWebId(i);
				this.participant.setTempId(i);
				log("web ID = " + i.toString());

			} else if(sm.startsWith("side=")) {
				Integer i = Integer.valueOf(sm.substring(5));
				if(i != 0) {
					PlayerSide ps = new PlayerSide(i);
					this.participant.setSide(ps);
					log("side = " + ps.toString());
				}
				
			} else if(sm.startsWith("wins=")) {
				this.winCount = sm.substring(5);
				
			} else if(sm.startsWith("play=")) {
				this.playCount = sm.substring(5);
				
			} else if(sm.equals("toomanyplayers")) {
				frame.launchInfoDialog("Sorry", "You cannot join as a player, " +
						"because this game already has 2 players. You will be disconnected.");
				disconnect();
				frame.dispose();
				
			} else if(sm.equals("alreadyindb")) {
					if(permissionToCreate)
					frame.launchInfoDialog("Sorry", "Cannot create such account, " +
						"because it already exists in the database of the server. You will be disconnected.");
				else
					frame.launchInfoDialog("Sorry", "Cannot log in to such account, " +
						"because someone (hopefully you) has already logged in using this login. " +
						"This duplicate will be disconnected.");
				disconnect();
				frame.dispose();
				
			} else if(sm.equals("badpassword")) {
				frame.launchInfoDialog("Sorry", "The password for this login is incorrect. " +
						"You will be disconnected.");
				disconnect();
				frame.dispose();
				
			} else if(sm.equals("nosuchaccount")) {
				if(!permissionToCreate) {
					frame.launchInfoDialog("Sorry", "Such account does not exist, " +
						"you will be disconnected. Please check the 'create new' box in the 'join to a game' form " +
						"if you wish to play.");
					disconnect();
					frame.dispose();
				} else send(new SystemMsg("permissionToCreate"));
				
			} else if(sm.equals("loggedin")) {
				if(!permissionToCreate)
					frame.launchInfoDialog("Hello", "Welcome back, " + participant.getName() + ".");
				
			} else if(sm.equals("accountcreated")) {
				frame.launchInfoDialog("Hello", "Welcome to TicTacToe server, " + participant.getName() + ". " +
						"Your records will be kept on this server. You can use chat. Have a nice R&R session.");
				send(participant);
			}

		} else if(o instanceof Game) {
			frame.setBoardContents(((Game)o).getBoard());
		} else if(o instanceof ChatMsg)
			frame.getLastChat().addEntry(((ChatMsg)o).toString());
	}

	@Override
	protected void doMoreActions(Xmlable o) {
		//nothing
	}
}
