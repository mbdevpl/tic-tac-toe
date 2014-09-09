package mb.tictactoe;

/**
 * Main class that initializes two instances: client and server.
 * @version 1.0
 * @author mb
 */
public class TicTacToe {
	/**
	 * Let's play the game!
	 * @param args 
	 */
	public static void main(String[] args) {
		(new ServerCreationFrame()).launch();
		(new ServerJoiningFrame()).launch();
	}
}
