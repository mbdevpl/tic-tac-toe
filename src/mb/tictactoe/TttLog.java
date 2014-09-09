package mb.tictactoe;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Log designed for the TicTacToe server.
 * @author mb
 */
public class TttLog extends JScrollPane {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = -211807634296343666L;

	/**
	 * Will new entries be added at the top of the text area?
	 */
	private static final boolean newFirst = true;
	
	/**
	 * Reference to the text area of log.
	 */
	private JTextArea log;
	
	/**
	 * A panel.
	 */
	JPanel panel;
	
	/**
	 * Constructor.
	 */
	public TttLog() {
		super();
		this.panel = new JPanel(new BorderLayout());
		this.log = new JTextArea("");
		
		log.setEditable(false);

		panel.add(log, BorderLayout.CENTER);
		getViewport().add(panel);
	}
	
	/**
	 * Adds time stamp to a given message.
	 * @param message
	 * @return 
	 */
	private String generateEntry(String message) {
		Date d = new Date();
		return "[" + d + "] " + message;
	}
	
	/**
	 * Adds entry to the log (appending the time stamp first).
	 * @param s the above-mentioned entry
	 */
	public void addEntry(String s) {
		String text = log.getText();
		if(text.equals(""))
			log.setText(generateEntry(s));
		else {
			if(newFirst)
				log.setText(generateEntry(s) + "\n" + text);
			else
				log.setText(text + "\n" + generateEntry(s));
		}
	}
}
