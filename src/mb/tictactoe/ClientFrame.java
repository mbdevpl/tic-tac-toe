package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import mb.tictactoe.data.Board;
import mb.tictactoe.data.CellData;
import mb.tictactoe.data.ChatMsg;
import mb.tictactoe.data.SystemMsg;
import mb.web.WebThreadRemoteClient;

/**
 * Client uses this to play the game and chat.
 * @author mb
 */
@SuppressWarnings("serial")
public class ClientFrame extends TttFrame {
	private WebThreadRemoteClient client;
	private TttBoard board;
	public JMenuItem oneMore;
	
	public volatile boolean movesActive = false;
	public volatile boolean playable = false;
	
	public ClientFrame(WebThreadRemoteClient client) {
		super("TicTacToe", 510, 10, 400, 600);
		this.client = client;
		
		addMenu("Game");
		addMenuItem("About");
		oneMore = addMenuItem("One more game");
		oneMore.setEnabled(false);
		addMenuItem("Exit");
		
		addMenu("User (" + this.client.participant.getName() + ")");
		addMenuItem("View my stats");
		addMenuItem("Delete from server");

		gb.weightx = 1;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addLabel("Game status: ");
		
		gb.weightx = 1;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		addLabel("status").setFont(font);
		getLastLabel().setText("");
		
		gb.weighty = 1;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.BOTH;
		board = addBoard();
		board.addMouseListener(this);
		//getLastBoard().setMinimumSize(dim);
		//getLastBoard().setPreferredSize(dim);

		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.weighty = 0;
		addChat();
	}
	
	public void mouseClicked(MouseEvent me) {
		if(playable && this.movesActive) {
			try {
				int col = board.getCol(me.getX());
				int row = board.getRow(me.getY());
				if(board.getContents().elementAt(col, row).isEmpty()) {
					CellData cd = new CellData(col, row, client.participant.getSide());
					//end of turn
					this.movesActive = false;
					client.send(cd);
					client.send(new SystemMsg("turnend"));
				}
			} catch(IllegalArgumentException e) {
				System.out.println("illegal argument exception when mouse clicked: " + e.getMessage());
			}
		}
	}
	
	public void setBoardContents(Board b) {
		board.setContents(b);
	}
	
	@Override
	protected void runCommand(String cmd) {
		//client.log("cmd=" + cmd);
		if(cmd.equals("About"))
			showAboutInfo();
		else if(cmd.equals("Send")) {
			String msg = getLastChat().getMsg();
			if(!msg.equals("")) {
				client.send(new ChatMsg(client.getWebId(), "CLIENT", client.participant.getName(), msg));
				getLastChat().clearMsg();
			}
			
		} else if(cmd.equals("One more game")) {
			client.send(new SystemMsg("onemoregame"));
			
		} else if(cmd.equals("Exit") || cmd.equals("frameClosed")) {
			client.log("closing & disconnectig...");
			client.disconnect();
			if(cmd.equals("Exit"))
				this.dispose();
			
		} else if(cmd.equals("View my stats")) {
			client.send(new SystemMsg("getCounts"));
			while(client.playCount == null || client.winCount == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { }
			}
			this.launchInfoDialog("Your current stats", "Just arrived from server... \n" +
					" times played: " + client.playCount + "\n" +
					" times won: " + client.winCount);
			client.playCount = null;
			client.winCount = null;
			
		} else if(cmd.equals("Delete from server")) {
			launchQuestionDialog("Confirmation requied",
					"Are you sure that you want to delete your account from server?");
			
		} else if(cmd.equals("dialogYes")) {
			client.send(new SystemMsg("deleteme"));
			client.disconnect();
			this.dispose();
		}/* else if(cmd.equals("dialogNo")) {
			client.send(new SystemMsg("deleteme"));
			client.disconnect();
		}*/
	}
	
	public void setStatus(String status) {
		this.getAsLabel("status").setText(status);
	}

}
