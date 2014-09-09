package mb.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import mb.tictactoe.data.CellData;
import mb.tictactoe.data.ChatMsg;
import mb.tictactoe.data.Game;
import mb.tictactoe.data.Participant;
import mb.tictactoe.data.SystemMsg;

/**
 * Abstract parent of server-side client and remote client. Contains common methods.
 * @author mb
 */
public abstract class WebThreadClient extends WebThread {
	protected Socket socket;
	private XmlReceiver in;
	private XmlSender out;

	private static boolean logTraffic = false;
	private volatile boolean disconnectSent = false;
	
	/**
	 * Constructs an instance using a ready socket. Used when initialising a server client.
	 * Communication is done via send() and listen() methods.
	 * @param prefix 
	 * @param socket 
	 * @throws IOException when connection could not be established before timeout
	 */
	public WebThreadClient(String prefix, Socket socket) throws IOException {
		super(prefix + "Client", socket.getInetAddress().getHostName(), socket.getPort(), socket.getSoTimeout());
		
		this.socket = socket;
		in = new XmlReceiver(this.socket.getInputStream());
		out = new XmlSender(this.socket.getOutputStream());
	}
	
	/**
	 * Constructs an instance, uses data to construct a socket.
	 * @param prefix thread name prefix
	 * @param hostname
	 * @param port
	 * @param timeout
	 * @throws IOException when something does not go well
	 */
	public WebThreadClient(String prefix, String hostname, int port, int timeout) throws IOException {
		super(prefix + "Client", hostname, port, timeout);
		//used by remote client
		socket = new Socket();
		socket.setSoTimeout(timeout);
		socket.connect(new InetSocketAddress(hostname, port));
		socket.setKeepAlive(true);
		in = new XmlReceiver(socket.getInputStream());
		out = new XmlSender(socket.getOutputStream());
		
		//log(String.valueOf(this.getTimeout()));
		//log(String.valueOf(socket.getSoTimeout()));
	}

	/**
	 * This method removes this client from the server's list, stops communication etc.
	 * Simply, it does exactly what its name indicates.
	 */
	public void disconnect() {
		if(!disconnectSent) {
			disconnectSent = true;
			send(new SystemMsg("disconnect"));
			stopMe();
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) {
				log("Already disconnected.");
			}
		}
	}
	
	/**
	 * Sends data to the other side.
	 * @param o data to be sent
	 */
	public void send(Xmlable o) {
		out.write(o.toXmlString() + "\n");
		out.flush();
		if(logTraffic && o instanceof SystemMsg) log("sent " + ((SystemMsg)o).toString());
	}
	
	/**
	 * Listens for messages from the other side.
	 * @return the message from the other side
	 * @throws IOException if timeout was reached before any communication occurred.
	 */
	private Xmlable receive() throws IOException {
		String s = null;
		
		s = in.readLine();
		//log(s);
		//try { sleep(1000); } catch (InterruptedException e) { }
		
		if(s == null || s.length() == 0) return null;
			//throw new SocketException("Message outside the protocol.");
		XmlTree tree = new XmlTree(s);
		String name = tree.getName();
		if(name.equals("systemmsg")) return new SystemMsg(tree);
		else if(name.equals("chatmsg")) return new ChatMsg(tree);
		else if(name.equals("participant")) return new Participant(tree);
		else if(name.equals("game")) return new Game(tree);
		else if(name.equals("celldata")) return new CellData(tree);
		else {
			log("received unsupported class: " + name);
			throw new IllegalArgumentException("cannot convert the XmlTree to object, unsupported class.");
		}
	}

	public void ping() {
		send(new SystemMsg("ping"));
		state = WAITING;
	}
	
	@Override
	protected void doActions() {
		//listening
		Xmlable o = null;
		try {
			o = receive();
			timeoutCounter = 0;
			if(logTraffic && o != null) log("received " + o.toString());
		} catch (SocketTimeoutException e) {
			if(state == WAITING) {
				timeoutCounter++;
				log("Timeout " + timeoutCounter  + "/" + maxTimeoutCount + ".");
				ping(); //one more try, but without reducing the counter.
			} //else System.out.println("Timeout.");
			if(timeoutCounter >= maxTimeoutCount) {
				log("Connection timeout, dropping client.");
				disconnect();
			}
		} catch (NoRouteToHostException e) {
			log("No route to host.");
			e.printStackTrace();
			disconnect();
		} catch (SocketException e) {
			log("Connection was dropped.");
			disconnect();
		} catch (IOException e) {
			log("I/O error.");
			stopMe();
		}
		//checking type of the message
		//if(o == null) throw new NullPointerException("null");
		/*boolean ok = false;
		if(o instanceof SystemMsg || o instanceof ChatMsg || o instanceof Board
				|| o instanceof Participant || o instanceof Game)
			ok = true;
		if(!ok) throw new IllegalArgumentException("Received an unsupported object via xml!");*/

		//interpretation
		if(o != null) {
			//log("TEST-TEST " + o.getClass().getSimpleName() + " " + o);
			if(o instanceof SystemMsg) { // standard system messages
				String s = ((SystemMsg)o).getMsg();
				if(s.equals("ping")) {
					send(new SystemMsg("pong"));
				} else if(s.equals("pong")) {
					log("Ping received.");
					state = LISTENING;
					timeoutCounter = 0;
				} else if(s.equals("disconnect")) {
					state = WAITING;
					disconnect();
				} else receivedAnotherObject(o); //nonstandard system messages
			} else receivedAnotherObject(o); //another objects
			
			doMoreActions(o);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof WebThreadClient) {
			WebThreadClient c = (WebThreadClient)o;
			if(c.getWebId() == this.getWebId() && c.socket.equals(this.socket))
				return true;
		}
		return false;
	}

	protected abstract void doMoreActions(Xmlable o);
	protected abstract void receivedAnotherObject(Xmlable o);
}
