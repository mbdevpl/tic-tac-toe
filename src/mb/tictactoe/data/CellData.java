package mb.tictactoe.data;

import mb.web.XmlTree;

/**
 * CellState + coordinates of a cell. 
 * @author mb
 */
public class CellData extends CellState {
	private int x;
	private int y;
	
	public CellData(int x, int y, int state) {
		this.setX(x);
		this.setY(y);
		this.state = state; 
	}

	public CellData(XmlTree tree) {
		super(tree);
		this.setX(Integer.parseInt(tree.getAttr("x")));
		this.setY(Integer.parseInt(tree.getAttr("y")));
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "[" + getSymbol() + " at (" + String.valueOf(getX()) + "," + String.valueOf(getY()) + ")]";
	}
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree tree = super.toXmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/celldata.dtd");
		tree.setName("celldata");
		
		tree.addAttribute("x", String.valueOf(getX()));
		tree.addAttribute("y", String.valueOf(getY()));
		
		return tree;
	}
	
}
