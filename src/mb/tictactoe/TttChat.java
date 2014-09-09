package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mb.graphics.GridBagPane;

/**
 * Chat, that has basic functionality of sending messages and viewing received messages list.
 * @author mb
 */
@SuppressWarnings("serial")
public class TttChat extends GridBagPane {
	private JTextArea chat;
	private JButton sendB;
	
	public TttChat(ActionListener al) {
		super("Chat");
		
		gb.weightx = 1;
		gb.weighty = 0.5;
		
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.BOTH;
		JScrollPane sp = new JScrollPane();
		cp.add(sp, gb);
		
		chat = new JTextArea("chathistory");
		chat.setName("TttChat: chathistory");
		chat.setEditable(false);
		chat.setText("Welcome to a simple Tic Tac Toe game!");
		sp.getViewport().add(chat);
		
		//dim.setSize(300, 50);
		//chat.setMinimumSize(dim);
		//chat.setPreferredSize(dim);

		gb.weighty = 0;
		gb.gridwidth = 1;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addTextField("chatmsg").setText("");
		getLastTextField().addActionListener(al);
		getLastTextField().setActionCommand("Send");
		
		gb.weightx = 0;
		gb.fill = GridBagConstraints.NONE;
		sendB = addButton("sendmsg", al);
		sendB.setText("Send");
		sendB.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
		//sendB.set

		dim.setSize(600, 150);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		
		this.validate();
	}
	
	public String getMsg() {
		return this.getLastTextField().getText();
	}
	
	public void addEntry(String s) {
		chat.setText(chat.getText() + "\n" + s);
	}
	
	public void clearMsg() {
		getLastTextField().setText("");
	}
}
