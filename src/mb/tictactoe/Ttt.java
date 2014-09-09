package mb.tictactoe;

import mb.tictactoe.data.Board;

/**
 * Class containing utility methods that correspond to game rules of Tic Tac Toe.
 * @author mb
 */
public class Ttt {
	/**
	 * Determines if a given board is full.
	 * @param b a given board
	 * @return true if every field of the board is nonempty, false otherwise
	 */
	public static boolean isFull(Board b) {
		for(int x = 0 ; x < b.getWidth(); x++)
			for(int y = 0; y < b.getHeight(); y++) {
				if(b.elementAt(x, y).isEmpty()) return false;
			}
		return true;
	}
	
	public static boolean gameOver(Board b) {
		if(!(winner(b) == ' ') || isFull(b))
			return true;
		return false;
	}
	
	public static char winner(Board b) {
		//checking both players, starting with 'x'
		for(int pl = 1; pl <= 2; pl++) {
			char currField = (pl == 1 ? 'x' : 'o');
			boolean over = false; //is the game over because someone won?
			//boolean currOver = false; //current state of game-over
			
			//checking vertical lines
			for(int x = 0 ; x < b.getWidth(); x++)
				for(int y = 0; y < b.getHeight() - b.getWinLen() + 1; y++) {
					over = true;
					for(int z = 0; z < b.getWinLen(); z++)
						if(b.elementAt(x, y + z).getSymbol() != currField) over = false;
					if(over) return currField;
				}
			
			//checking horizontal lines
			for(int x = 0 ; x < b.getWidth() - b.getWinLen() + 1; x++)
				for(int y = 0; y < b.getHeight(); y++) {
					over = true;
					for(int z = 0; z < b.getWinLen(); z++)
						if(b.elementAt(x + z, y).getSymbol() != currField) over = false;
					if(over) return currField;
				}
			
			//checking (x,x) diagonals
			for(int x = 0 ; x < b.getWidth() - b.getWinLen() + 1; x++)
				for(int y = 0; y < b.getHeight() - b.getWinLen() + 1; y++) {
					over = true;
					for(int z = 0; z < b.getWinLen(); z++)
						if(b.elementAt(x + z, y + z).getSymbol() != currField) over = false;
					if(over) return currField;
				}
			
			
			//checking (x,-x) diagonals
			for(int x = b.getWinLen() - 1 ; x < b.getWidth(); x++)
				for(int y = 0; y < b.getHeight() - b.getWinLen() + 1; y++) {
					over = true;
					for(int z = 0; z < b.getWinLen(); z++)
						if(b.elementAt(x - z, y + z).getSymbol() != currField) over = false;
					if(over) return currField;
				}
		}
		
		return ' ';
	}
	
	/*public static CellData getAiMove(Board b, PlayerSide aiSide) {
		CellData cd = null;
		cd = new CellData(0, 0, 'x');
		return cd;
	}*/
}
