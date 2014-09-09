package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingConstants;

import mb.graphics.GridBagPane;
import mb.web.WebThread;
import mb.web.XmlTree;

/**
 * Pane containing hostname, port and timeout for user/admin to enter when connecting to/creating server.
 * @author mb
 */
@SuppressWarnings("serial")
public class TttServerDataPane extends GridBagPane {
	private String host, port, timeout;
	private String filePath;
	
	public TttServerDataPane(String filePath) {
		super("serverdata");
		
		this.filePath = filePath;
		host = WebThread.defaultHostname;
		port = String.valueOf(WebThread.defaultPort);
		timeout = String.valueOf(WebThread.defaultTimeout);
		
		try {
			XmlTree tree = XmlTree.createFromFile(filePath);
			host = tree.getAttr("hostname");
			port = tree.getAttr("port");
		} catch (FileNotFoundException e) {
		} catch (IOException e) { }
		
		gb.ipadx = 3;
		gb.ipady = 3;
		
		gb.weightx = 0;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		
		addLabel("Server coordinates");

		gb.fill = GridBagConstraints.NONE;
		gb.gridwidth = 1;
		addLabel("Hostname: ").setFont(font);
		getLastLabel().setHorizontalAlignment(SwingConstants.RIGHT);
		
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.weightx = 1;
		addTextField(host);
		
		gb.anchor = GridBagConstraints.LINE_END;
		gb.gridwidth = 1;
		gb.weightx = 0;
		addLabel("Port: ").setFont(font);
		getLastLabel().setHorizontalAlignment(SwingConstants.RIGHT);
		
		//gb.anchor = GridBagConstraints.CENTER;
		//gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.weightx = 1;
		addTextField(port);
		
		gb.gridwidth = 1;
		gb.weightx = 0;
		addLabel("Timeout (ms): ").setFont(font);
		getLastLabel().setHorizontalAlignment(SwingConstants.RIGHT);

		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.weightx = 1;
		addTextField(timeout);
		
		this.saveToFile();
	}
	
	protected String getHostname() {
		return getAsTextField(host).getText();
	}
	
	protected int getPort() throws NumberFormatException {
		int port = Integer.parseInt(getAsTextField(this.port).getText());
		if(port < 0)
			throw new NumberFormatException("Because entered port does not exist");
		return port;
	}
	
	protected int getTimeout() throws NumberFormatException {
		int timeout = Integer.parseInt(getAsTextField(this.timeout).getText());
		if(timeout < 100 || timeout > 10000)
			throw new NumberFormatException("Because timeout was out of constraints, which are <100, 10000>");
		return timeout;
	}

	
	public void saveToFile() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL(TicTacToeConstants.DTD_PATH + "serverdata.dtd");
		tree.setName("serverdata");
		tree.addAttribute("hostname", getHostname());
		tree.addAttribute("port", getAsTextField(this.port).getText());
		tree.saveToFile(filePath);
	}

}
