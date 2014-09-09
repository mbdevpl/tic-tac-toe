package mb.web;

import java.io.IOException;
import java.net.Socket;

import mb.tictactoe.data.Participant;
import mb.tictactoe.data.SystemMsg;

/**
 * Sub-thread created by WebThreadServer in case any connection comes. It has no frame assigned.
 * It is permanently connected to the WebThreadRemoteClient via Socket. Xml files are used for 
 * data exchange.
 * @author mb
 */
public class WebThreadServerClient extends WebThreadClient {
	/**
	 * To this server the client is connected directly.
	 */
	protected WebThreadServer server;
	
	/**
	 * Using this data, a remote client connected to the server.
	 */
	public volatile Participant participant;
	
	/**
	 * Constructs a server-side client thread. 
	 * @param server server that have constructed this thread
	 * @param clientSocket socket via which communication with remote client is done
	 * @throws IOException 
	 */
	public WebThreadServerClient(WebThreadServer server, StreamSide pipeSide, Socket clientSocket) throws IOException {
		super("server", clientSocket);
		this.server = server;
		setWebId(server.getNextClientId());
		ping();
	}

	@Override
	public void log(String s) {
		server.log(this + ": " + s);
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		server.remove(this);
		server.game.removeParticipant(this.getWebId());
		server.game.refreshRemoteData(server);
	}
	
	@Override
	protected void receivedAnotherObject(Xmlable o) {
		server.receivedObject(this, o);
	}

	@Override
	protected void doMoreActions(Xmlable o) {
		if(o instanceof SystemMsg) {
			SystemMsg s = (SystemMsg)o;
			if(s.getMsg().equals("disconnect"))
				server.game.removeParticipant(this.getWebId());
		}
	}
	
	public String toString() {
		if(participant != null)
			return "client(" + this.getWebId() + "," + this.participant.getName() + ")";
		else
			return "client(" + this.getWebId() + ",)";
	}
	
	/**
	 * Returns login used by the user that caused this thread to start.
	 * @return login of the user
	 */
	public String getParticipantName() {
		return participant.getName();
	}
}
