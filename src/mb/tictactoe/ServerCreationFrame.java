package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketException;

import javax.swing.JOptionPane;

import mb.web.WebThreadServer;

/**
 * Allows creation of TicTacToe server.
 * @author mb
 */
public class ServerCreationFrame extends TttFrame {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = -144954996151622236L;
	private TttServerDataPane pane = null;
	private TttAdminDataPane adminData = null;
	
	public ServerCreationFrame() {
		super("TicTacToe connection manager", 10, 260, 400, 162);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);

		gb.ipadx = 3;
		gb.ipady = 3;

		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.weightx = 1;
		pane = addServerDataPane(TicTacToeConstants.SERVER_SERVER_PATH);
		
		adminData = new TttAdminDataPane(TicTacToeConstants.SERVER_DATA_PATH);
		this.addComponent(adminData);

		gb.gridwidth = 1;
		addButton("Create server", this);
		//addButton("Reset fields", this);
		addButton("Help for beginners", this);
	}

	@Override
	protected void runCommand(String cmd) {
		if(cmd.equals("frameClosing")) {
			pane.saveToFile();
			adminData.saveToFile();
			
		} if(cmd.equals("Reset fields")) {
			launchInfoDialog("Reset fields", "Sorry, this will be included in future versions.");
			
		} else if(cmd.equals("Help for beginners")) {
			launchInfoDialog("Help for beginners", " Quick guide how to begin:\n" +
					"1) Enter the hostname the same as your internal IP to play on LAN.\n" +
					"2) Change width/height/winning lengh if you like.\n" +
					"3) Click create. When 2 players join, the game will start.\n" +
					"4) You can observe what is happening via log, and you can always kick them the hell out.");
			
		} else try {
			if(cmd.equals("Create server")) {
				WebThreadServer server = new WebThreadServer(pane.getHostname(), pane.getPort(), pane.getTimeout(),
						adminData.getBoardWidth(), adminData.getBoardHeight(), adminData.getWinLen());
				server.start();
			}
		} catch (NumberFormatException e) {
			launchInfoDialog("Number format exception", e.getMessage() + ", there was an arror.\n" +
					"Both port and timeout must have proper values.");
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Error with " + e.getMessage() + ".\n" +
					"Both port and timeout must have proper values.");
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(this, "Cannot connect to selected host.");
		} catch (PortUnreachableException e) {
			JOptionPane.showMessageDialog(this, "The port is unreachable.");
		} catch (BindException e) {
			JOptionPane.showMessageDialog(this, "Cannot create the server at the selected address. Maybe:\n" +
							"a) port is occupied, try a different one\n" +
							"b) hostname is wrong, try 'localhost', or this machine's current IP");
		} catch (NoRouteToHostException e) {
			JOptionPane.showMessageDialog(this, "Cannot find the route to host.");
		} catch (SocketException e) {
			JOptionPane.showMessageDialog(this, "Could not establish a connection to the given address. Maybe:\n" +
							"a) port is occupied, try a different one\n" +
							"b) hostname is wrong, try 'localhost', or this machine's current IP");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to create a server. Maybe:\n" +
					"a) port is occupied, try a different one\n" +
					"b) hostname is wrong, try 'localhost', or this machine's current IP");
		}
	}
}
