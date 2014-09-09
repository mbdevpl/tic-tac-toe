package mb.tictactoe;

import java.awt.GridBagConstraints;

import mb.tictactoe.data.ChatMsg;
import mb.tictactoe.data.Participant;
import mb.web.WebThreadServer;

/**
 * Used to manage a running server. Contains log and chat archive.
 * @author mb
 */
@SuppressWarnings("serial")
public class ServerFrame extends TttFrame {
	private WebThreadServer server;
	
	public ServerFrame(WebThreadServer server) {
		super("TicTacToe Server", 750, 180, 600, 500);
		this.setResizable(true);
		this.server = server;
		
		addMenu("App");
		addMenuItem("About");
		addMenuItem("Exit");
		addMenu("Server");
		addMenuItem("Show information");
		addMenuItem("List participants");
		addMenuItem("Ping...");
		addMenuItem("Ping all");
		addMenuItem("Kick all");
		addMenuItem("Stop");

		gb.gridheight = 1;
		gb.weightx = 1;
		gb.weighty = 0;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addLabel("Log:");
		
		gb.weighty = 2;
		gb.fill = GridBagConstraints.BOTH;
		addLog();
		//addLog(logPipe.addReceiver());
		//getLastLog().start();

		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addLabel("Chat lookup:");
		gb.weighty = 0;
		gb.fill = GridBagConstraints.BOTH;
		addChat();//.setReceiver(chatPipe.addReceiver());
	}
	
	/**
	 * Adds an entry to log.
	 * @param s description of the entry 
	 */
	public void log(String s) {
		getLastLog().addEntry(s);
		//getLastTextArea().setText(s + "\n" + getLastTextArea().getText());
	}
	
	/**
	 * Listens for menu events.
	 */
	@Override
	protected void runCommand(String cmd) {
		if(cmd.equals("About"))
			showAboutInfo();
		
		else if(cmd.equals("Exit") || cmd.equals("frameClosed")) {
			server.disconnectAll();
			server.close();
			if(cmd.equals("Exit"))
				this.dispose();
			
		} else if(cmd.equals("Show information"))
			this.launchInfoDialog("Information about this server",
					"This server is " + (server.isAlive()?"running":"dead") + ".\n"
					+ " hostname=" + server.getHostname() + "\n"
					+ " port=" + server.getPort() + "\n"
					+ " timeout=" + server.getTimeout() + "\n");
		
		else if(cmd.equals("List participants")) {
			new TttFrame("List of participants", this.getX(), this.getY(), 250, 400) {
				@Override
				protected void runCommand(String cmd) {
					if(cmd.equals("frameOpened")) {
						this.gb.fill = GridBagConstraints.BOTH;
						this.gb.weightx = 1;
						this.gb.weighty = 1;
						this.addComponent(new GameParticipants(server.game.getParticipants(), this, "Ok"));
						
						this.launch();
					} else if(cmd.equals("Ok"))
						this.dispose();
				}
			}.launch();
			
		} else if(cmd.equals("Ping...")) {
			new TttFrame("Select participant to ping", this.getX(), this.getY(), 250, 400) {
				private GameParticipants gp;
				
				@Override
				protected void runCommand(String cmd) {
					if(cmd.equals("frameOpened")) {
						gp = new GameParticipants(server.game.getParticipants(), this, "Ping");
						
						this.gb.fill = GridBagConstraints.BOTH;
						this.gb.weightx = 1;
						this.gb.weighty = 1;
						this.addComponent(gp);
						
						this.launch();
					} else if(cmd.equals("Ping")) {
						for(Participant p: this.gp.getSelectedParticipants())
							server.ping(server.getWebIdFor(p));
						this.dispose();
						
					} else if(cmd.equals("Cancel")) {
						this.dispose();
					}
				}
			}.launch();
			
		} else if(cmd.equals("Ping all"))
			server.pingAll();
		
		//else if(cmd.equals("Kick..."))
		//	;
		else if(cmd.equals("Kick all"))
			server.disconnectAll();
		//else if(cmd.equals("Mute..."))
		//	;
		else if(cmd.equals("Stop"))
			server.close();
		else if(cmd.equals("Send")) {
			server.sendToAll(new ChatMsg(server.getWebId(), "SERVER", "admin", this.getLastChat().getMsg()));
			getLastChat().clearMsg();
		}
		//else if(!cmd.startsWith("frame"))
		//	System.out.println("cmd=" + cmd);
	}
}
