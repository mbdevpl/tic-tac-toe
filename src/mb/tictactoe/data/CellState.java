package mb.tictactoe.data;

import mb.web.XmlTree;

/**
 * Basicly just non-abstract child-copy of <code>EmptyOrCrossOrNought</code>.
 * @author mb
 */
public class CellState extends EmptyOrCrossOrNought {
	/**
	 * Default constructor. Gives value 0 to the cell state.
	 */
	public CellState() {
		super(0);
	}
	
	/**
	 * Constructs new object from a char.
	 * @param symbol ' ' (space), 'x' or 'o' (lower-case or upper-case)
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public CellState(char symbol) {
		super(symbol);
	}
	
	/**
	 * Constructs new object from an int.
	 * @param number 0, 1 or 2
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public CellState(int number) {
		super(number);
	}

	/**
	 * Copy constructor.
	 * @param cell source of data
	 * @throws IllegalArgumentException when argument's state is out of the scope
	 */
	public CellState(CellState cell) {
		super(cell);
	}
	
	public CellState(XmlTree xml) {
		super(xml.getAttr("val").charAt(0));
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CellState)
			return ( ( (CellState)o ).getState() == getState() );
		else return false;
	}
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/cell.dtd");
		tree.setName("cell");
		tree.addAttribute("val", "" + this.getSymbol());
		
		return tree;
	}
	
}
