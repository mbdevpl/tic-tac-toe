package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketException;

import mb.tictactoe.data.Participant;
import mb.web.WebThreadRemoteClient;

@SuppressWarnings("serial")
public class ServerJoiningFrame extends TttFrame {
	
	private TttServerDataPane pane = null;
	private TttClientDataPane clientData = null;

	public ServerJoiningFrame() {
		super("TicTacToe: Join to a game", 10, 10, 400, 230);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);

		gb.ipadx = 3;
		gb.ipady = 3;
		
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.weightx = 1;
		pane = addServerDataPane(TicTacToeConstants.CLIENT_SERVER_PATH);
		
		clientData = new TttClientDataPane(TicTacToeConstants.CLIENT_DATA_PATH);
		this.addComponent(clientData);
		
		gb.gridwidth = 1;
		addButton("Join server", this);
		//addButton("Reset fields", this);
		addButton("Help for beginners", this);
	}

	@Override
	protected void runCommand(String cmd) {
		if(cmd.equals("frameClosing")) {
			pane.saveToFile();
			clientData.saveTo(TicTacToeConstants.CLIENT_DATA_PATH);
			
		} else if(cmd.equals("Reset fields")) {
			launchInfoDialog("Reset fields", "Sorry, this will be included in future versions.");
			
		} else if(cmd.equals("Help for beginners")) {
			launchInfoDialog("Help for beginners", " Quick guide how to begin:\n" +
					"1) Enter the exact same hostname and port as in server instance.\n" +
					"2) Enter some nickname and password. Check 'create new' box.\n" +
					"3) Click join. When a 2nd player joins, the game will start.\n" +
					"4) When a game finishes, you can play once more by selecting the proper option " +
					"in the game's window menu.");
			
		} else if(cmd.equals("frameClosing")) {
			clientData.saveTo(TicTacToeConstants.CLIENT_DATA_PATH);
			
		} else try {
			if(cmd.equals("Join server")) {
				Socket socket = new Socket();
				socket.setSoTimeout(pane.getTimeout());
				socket.connect(new InetSocketAddress(pane.getHostname(), pane.getPort()));
				socket.setKeepAlive(true);
				
				//WebThreadRemoteClient client = new WebThreadRemoteClient(pane.getHostname(), pane.getPort(), pane.getTimeout());
				WebThreadRemoteClient client = new WebThreadRemoteClient(socket, 
						new Participant(clientData.getNickname(), clientData.getPassword(), clientData.getKind()), 
						clientData.createNew());
				client.start();
			}
		} catch (NumberFormatException e) {
			launchInfoDialog("Number format exception", e.getMessage() + ", there was an arror.\n" +
					"Both port and timeout must have proper values.");
		} catch (IllegalArgumentException e) {
			launchInfoDialog("Illegal argument exception", "Error with " + e.getMessage() + ".\n" +
					"Both port and timeout must have proper values.");
		} catch (ConnectException e) {
			launchInfoDialog("Connect exception", "Cannot connect to selected host.");
		} catch (PortUnreachableException e) {
			launchInfoDialog("Port unreachable exception", "The port is unreachable.");
		} catch (BindException e) {
			launchInfoDialog("Bind exception", "Cannot create the server at the selected address. Maybe:\n" +
							"a) port is occupied, try a different one\n" +
							"b) hostname is wrong, try 'localhost', or this machine's current IP");
		} catch (NoRouteToHostException e) {
			launchInfoDialog("No route to host exception", "Cannot find the route to host.");
		} catch (SocketException e) {
			launchInfoDialog("Socket exception", "Could not establish a connection to the given address. Maybe:\n" +
							"a) port is occupied, try a different one\n" +
							"b) hostname is wrong, try 'localhost', or this machine's current IP");
		} catch (IOException e) {
			launchInfoDialog("IO exception", "There was an I/O problem while connecting." +
					" Unable to connect to game server. Maybe:\n" +
					"a) you are not connected to any network, try connecting to a network first\n" +
					"b) the application is blocked by firewall, try to open the port in system's or router's firewall\n" +
					"c) the machine defined by this data does not exist, or some data were misspelled\n" +
					"d) the server is simply offline");
		}
	}

}
