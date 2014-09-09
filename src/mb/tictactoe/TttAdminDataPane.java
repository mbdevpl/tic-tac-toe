package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingConstants;

import mb.graphics.GridBagPane;
import mb.web.XmlTree;

/**
 * Contains data used to initialise server properly (basic properties of the board).
 * @author mb
 */
public class TttAdminDataPane extends GridBagPane {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = 4097313981373445356L;

	private String filePath;
	
	public TttAdminDataPane(String filePath) {
		super("admindata");
		this.filePath = filePath;
		String width, height, winLen;
		width = "3";
		height = "3";
		winLen = "3";
		
		try {
			XmlTree tree = XmlTree.createFromFile(filePath);
			
			width = tree.getAttr("width");
			height = tree.getAttr("height");
			winLen = tree.getAttr("winlen");
			
		} catch (FileNotFoundException e) {
		} catch (IOException e) { }
		
		gb.weightx=0;
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addLabel("Admininstation data");
		
		gb.gridwidth = 1;
		addLabel("Admin nickname: ").setFont(font);
		gb.gridwidth = GridBagConstraints.REMAINDER;
		addTextField("nickname").setText("admin");
		getLastTextField().setEditable(false);
		
		gb.gridwidth = 1;
		addLabel("Board: ").setFont(font);

		gb.weightx=0;
		addLabel(" width = ").setHorizontalAlignment(SwingConstants.RIGHT);
		getLastLabel().setFont(font);
		gb.weightx=1;
		addTextField("boardWidth").setText(width);
		
		addLabel("height = ").setHorizontalAlignment(SwingConstants.RIGHT);
		getLastLabel().setFont(font);
		gb.weightx=1;
		addTextField("boardHeight").setText(height);
		
		gb.weightx=0;
		addLabel(" winning length = ").setHorizontalAlignment(SwingConstants.RIGHT);
		getLastLabel().setFont(font);
		gb.weightx=1;
		addTextField("boardWinLen").setText(winLen);
		
		this.saveToFile();
	}
	
	private String getWidthStr() {
		return this.getAsTextField("boardWidth").getText();
	}
	
	public int getBoardWidth() {
		return Integer.parseInt(getWidthStr());
	}
	
	private String getHeightStr() {
		return this.getAsTextField("boardHeight").getText();
	}
	
	public int getBoardHeight() {
		return Integer.parseInt(getHeightStr());
	}
	
	public int getWinLen() {
		return Integer.parseInt(getWinLenStr());
	}

	
	private String getWinLenStr() {
		return this.getAsTextField("boardWinLen").getText();
	}
	
	public void saveToFile() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL(TicTacToeConstants.DTD_PATH + "admindata.dtd");
		tree.setName("admindata");
		tree.addAttribute("width", getWidthStr());
		tree.addAttribute("height", getHeightStr());
		tree.addAttribute("winlen", getWinLenStr());
		tree.saveToFile(filePath);
	}
}
