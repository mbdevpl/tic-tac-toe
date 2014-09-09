package mb.tictactoe;

import mb.graphics.GridBagFrame;

/**
 * Superclass of 4 main frame classes. Contains some commonly used methods.
 * Those 4 classes are: ClientFrame, ServerFrame, ServerCreationFrame and ServerJoiningFrame.
 * @author mb
 */
public abstract class TttFrame extends GridBagFrame {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = 6059714144540770266L;

	public TttFrame(String title, int x, int y, int width, int height) {
		super(title, x, y, width, height);
	}

	public void showAboutInfo() {
		this.launchInfoDialog("About Tic Tac Toe", "Author:\n Mateusz Bysiek,\n" +
				" Computer Science A/R, MiNI, Warsaw University of Technology\n" +
				" 2010-2011\nThis applet is published under GNU GPL v.2.0.\n");
	}
	
	protected TttBoard addBoard() {
		TttBoard board = new TttBoard();
		board.setName("TttBoard");
		addComponent(board);
		return board;
	}
	public TttBoard getLastBoard() {
		return (TttBoard)getLastByType("TttBoard");
	}
	
	protected TttServerDataPane addServerDataPane(String path) {
		TttServerDataPane pane = new TttServerDataPane(path);
		pane.setName("ServerDataPane");
		addComponent(pane);
		return pane;
	}
	public TttServerDataPane getLastServerDataPane() {
		return (TttServerDataPane)getLastByType("ServerDataPane");
	}
	
	protected TttLog addLog() {
		TttLog log = new TttLog();
		log.setName("TttLog");
		addComponent(log);
		return log;
	}
	public TttLog getLastLog() {
		return (TttLog)getLastByType("TttLog");
	}
	
	protected TttChat addChat() {
		TttChat chat = new TttChat(this);
		chat.setName("TttChat");
		addComponent(chat);
		return chat;
	}
	public TttChat getLastChat() {
		return (TttChat)getLastByType("TttChat");
	}
}
