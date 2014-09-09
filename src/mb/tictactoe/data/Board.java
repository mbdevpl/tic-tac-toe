package mb.tictactoe.data;

import java.util.Vector;

import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * Game board, with size, winning length, and contents.
 * @author mb
 */
public class Board extends Xmlable {
	private int width, height, winLen;
	private Vector< Vector<CellState> > contents;
	
	public Board(XmlTree data) {
		this(Integer.parseInt(data.getAttr("width")), 
				Integer.parseInt(data.getAttr("height")), 
				Integer.parseInt(data.getAttr("winlen")));
		
		//System.out.println(data/*.toXmlString()*/);

		CellState[][] arr = new CellState[width][height];
		for(int y = 0; y < getHeight(); y++) {
			XmlTree rowData = data.getSubNode("row", y);
			//System.out.println(rowData.toString());
			//System.out.println(rowData.toXmlString());
			for(int x = 0; x < getWidth(); x++) {
				arr[x][y] = new CellState(rowData.getSubNode("cell", x));
			}
		}
		
		this.contents = new Vector< Vector<CellState> >();
		for(int x = 0; x < getWidth(); x++) {
			contents.add(new Vector<CellState>());
			for(int y = 0; y < getHeight(); y++)
				contents.elementAt(x).add(arr[x][y]);
		}
		
		//System.out.println(this.toString() + " @toBoard");
	}

	/**
	 * Creates a board with specified width and height, and fills it with default values.
	 * @param width
	 * @param height
	 * @param winLen 
	 */
	public Board(int width, int height, int winLen) {
		this.width = width;
		this.height = height;
		this.winLen = winLen;
		reinit();

		//version 1
		/*for(int i = 0; i < getHeight(); i++)
			set(i, i, new CellState('x'));
		for(int i = 0; i < getHeight(); i++)
			set(getHeight()-1-i, i, new CellState('o'));*/
		
		//version 2
		/*for(int i = 0; i < getHeight(); i++) {
			set(0, i, new CellState('x'));
			set(getWidth()-1, i, new CellState('o'));
		}
		set(0, 0, new CellState('o'));
		set(getWidth()-1, getHeight()-1, new CellState('x'));*/
		
		//version 3
		/*for(int i = 0; i < getHeight(); i++)
			set(i, i, new CellState('x'));
		for(int i = 0; i < getHeight(); i++)
			set(getHeight()-1-i, i, new CellState('o'));
		set(0, 0, new CellState('o'));
		set(0, getHeight()-1, new CellState('x'));*/
	}

	/*public Board(Vector< Vector<CellState> > contents) {
		width = contents.size();
		if(width == 0) throw new IllegalArgumentException();
		height = contents.elementAt(0).size();
		this.contents = contents;
	}

	public Board(Board board) {
		contents = new Vector< Vector<CellState> >(board.contents);
	}
	
	public Board(Object o) {
		this(((Board)o).width, ((Board)o).height);
	}*/
	
	public CellState elementAt(int x, int y) {
		return contents.elementAt(x).elementAt(y);
	}
	
	public void set(int x, int y, CellState value) {
		contents.elementAt(x).set(y, value);
	}
	
	public void fillWith(CellState value) {
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < getWidth(); x++)
				set(x, y, value);
	}
	
	/*public void copyFrom(Object o) {
		if(o instanceof Board) {
			Board bd = (Board)o;
			width = bd.width; 
			height = bd.height;
			reinit();
			for(int y = 0; y < getHeight(); y++)
				for(int x = 0; x < getWidth(); x++) {
					//CellState st = new CellState();
					//System.out.println(x + "," + y + "=" + board.elementAt(x, y).getClass());
					this.set(x, y, bd.elementAt(x, y));
				}
		}
	}*/
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	protected void reinit() {
		contents = new Vector< Vector<CellState> >();
		for(int i=0; i<getWidth(); i++) {
			contents.add(new Vector<CellState>());
			for(int j=0; j<getHeight(); j++) {
				contents.elementAt(i).add(new CellState());
			}
		}
	}
	
	@Override
	public String toString() {
		String vals = "";
		for(int y = 0; y < getHeight(); y++) {
			vals += "[";
			for(int x = 0; x < getWidth(); x++) {
				vals += elementAt(x, y);
				if(x < getWidth() - 1) vals += ", ";
			}
			vals += "]";
			if(y < getHeight() - 1) vals += "\n";
		}
		return vals + ", size=" + getWidth() + "x" + getHeight();
	}

	public XmlTree toXmlTree() {
		//System.out.println(this.toString() + " @toTree");
		
		XmlTree tree = new XmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/board.dtd");
		tree.setName("board");
		
		tree.addAttribute("width", String.valueOf(getWidth()));
		tree.addAttribute("height", String.valueOf(getHeight()));
		tree.addAttribute("winlen", String.valueOf(getWinLen()));
		
		for(int y = 0; y < getHeight(); y++) {
			XmlTree rowData = new XmlTree("<row></row>");
			for(int x = 0; x < getWidth(); x++) {
				XmlTree t = this.elementAt(x, y).toXmlTree();
				t.setDtdURL("");
				rowData.addSubNode(t, x);
			}
			tree.addSubNode(rowData, y);
			//System.out.println("ROWDATA = " + rowData.toString());
		}
		
		//System.out.println(tree.toXmlString());
		
		return tree;
	}
	
	/*public void setWinLen(int winLen) {
		this.winLen = winLen;
	}*/
	
	public int getWinLen() {
		return winLen;
	}

	
	public boolean isEmpty() {
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < getWidth(); x++)
				if(!elementAt(x, y).isEmpty())
					return false;
		return true;
	}

	public int getCurrPlayer() {
		int nonEmpty = 0;
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < getWidth(); x++)
				if(!elementAt(x, y).isEmpty())
					nonEmpty++;
		return (nonEmpty%2 == 0 ? 1 : 2);
	}
}
