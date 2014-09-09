package mb.tictactoe;

import java.awt.Graphics;

import mb.graphics.GridBagPane;
import mb.tictactoe.data.Board;

/**
 * Contains game board. Game is played with use of mouse. Board can resize to fit available space.
 * @author mb
 */
public class TttBoard extends GridBagPane {
	/**
	 * ID
	 */
	private static final long serialVersionUID = -7916705940260314985L;
	/**
	 * Global margin around a board.
	 */
	private final int MARGIN = 10;
	private int colsNo = 3, rowsNo = 3;
	/**
	 * All in pixels.
	 */
	private int cellJump = 0, cellMargin = 2, topMargin = 0, leftMargin = 0;
	
	private Board b = null;
	
	public TttBoard() {
		super("Board");
	}
	
	public void paintBoard(Graphics g) {
		g.drawRect(MARGIN + leftMargin, MARGIN + topMargin, colsNo*cellJump, rowsNo*cellJump);
		for(int i = 1; i< colsNo; i++)
			g.drawLine(MARGIN + leftMargin + i*cellJump, MARGIN + topMargin,
					MARGIN + leftMargin + i*cellJump, MARGIN + topMargin + rowsNo*cellJump);
		for(int i = 1; i< rowsNo; i++)
			g.drawLine(MARGIN + leftMargin, MARGIN + topMargin + i*cellJump,
					MARGIN + leftMargin + colsNo*cellJump, MARGIN + topMargin + i*cellJump);
		//for(int i = 0; i < rowsNo && i < colsNo; i++) drawX(g,i,i);
		//for(int i = 1; i < colsNo; i++) drawO(g,i,0);
		
		//drawSymbol(g, 0, 0, 'x');
		//drawSymbol(g, 2, 2, 'o');
		
		if(b != null) {
			//System.out.println("!!!board is not null");
			colsNo = b.getWidth();
			rowsNo = b.getHeight();
			for(int j = 0; j < rowsNo; j++)
				for(int i = 0; i < colsNo; i++)
					drawSymbol(g, i, j, b.elementAt(i, j).getSymbol());
		}
		
	}
	
	/**
	 * Draws X symbol in a given cell in this board.
	 * @param g given graphics
	 * @param col column
	 * @param row row
	 */
	public void drawX(Graphics g, int col, int row) {
		int offsetX = MARGIN + leftMargin + col*cellJump + cellMargin,
			offsetY = MARGIN + topMargin + row*cellJump + cellMargin,
			size = cellJump - 2*cellMargin;
		//shape of X
		g.drawLine(offsetX, offsetY, offsetX + size, offsetY + size);
		g.drawLine(offsetX + size, offsetY, offsetX, offsetY + size);
	}
	
	/**
	 * Draws O symbol in a given cell in this board.
	 * @param g given graphics
	 * @param col column
	 * @param row row
	 */
	public void drawO(Graphics g, int col, int row) {
		int offsetX = MARGIN + leftMargin + col*cellJump + cellMargin,
			offsetY = MARGIN + topMargin + row*cellJump + cellMargin,
			size = cellJump - 2*cellMargin;
		//shape of O
		g.drawOval(offsetX, offsetY, size, size);
	}
	
	public void drawSymbol(Graphics g, int col, int row, char symbol) {
		//Graphics g = this.getGraphics();
		//System.out.println("!!! drawing " + symbol);
		if(symbol == 'o' || symbol == 'O')
			drawO(g, col, row);
		else if(symbol == 'x' || symbol == 'X')
			drawX(g, col, row);
	}
	
	public int getCol(int x) {
		if(x < 0 || x > getHeight())
			throw new IllegalArgumentException("The x coord. of the click is out of bounds.");
		for(int col = 0; col < colsNo; col++)
			if(x > MARGIN + leftMargin + col*cellJump + cellMargin
					&& x < MARGIN + leftMargin + (col+1)*cellJump - cellMargin)
				return col;
		throw new IllegalArgumentException("The x coordinate of the click is between columns.");
	}
	
	public int getRow(int y) {
		if(y < 0 || y > getWidth())
			throw new IllegalArgumentException("The y coord. of the click is out of bounds.");
		for(int row = 0; row < rowsNo; row++)
			if(y > MARGIN + topMargin + row*cellJump + cellMargin
					&& y < MARGIN + topMargin + (row+1)*cellJump - cellMargin)
				return row;
		throw new IllegalArgumentException("The y coordinate of the click is between rows.");
	}
	
	private void setDimensions() {
		int min = cp.getHeight();
		topMargin = 0;
		leftMargin = (int)( (cp.getWidth() - cp.getHeight()) / 2 );
		if(getWidth() < min) {
			min = cp.getWidth();
			topMargin = (int)( (cp.getHeight() - cp.getWidth()) / 2 );
			leftMargin = 0;
		}
		cellJump = (int)( (min - 2*MARGIN) / colsNo );
//		System.out.println("height=" + cp.getHeight() + " cols=" + colsNo);
//		System.out.println("width=" + cp.getWidth() + " rows=" + rowsNo + " jump=" + cellJump);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		setDimensions();
		paintBoard(g);
	}
	
	public void setContents(Board board) {
		colsNo = board.getWidth();
		rowsNo = board.getHeight();
		b = board;
		this.setDimensions();
		this.repaint();
	}

	
	public Board getContents() {
		return b;
	}

}
