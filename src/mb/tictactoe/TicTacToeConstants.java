package mb.tictactoe;

/**
 * Class holding some constants, mainly file paths. 
 * @author mb
 */
public class TicTacToeConstants {
	/**
	 * Directory for .dtd files.
	 */
	public static final String DTD_PATH = "http://mbdev.pl/files/tictactoe/";
	
	/**
	 * Saves last used credentials.
	 */
	public static final String CLIENT_DATA_PATH = "TicTacToe_client_settings.xml";
	/**
	 * Saves last used hostname and port for joining server.
	 */
	public static final String CLIENT_SERVER_PATH = "TicTacToe_client_hostname.xml";
	
	/**
	 * Height, width and winning length last used for creating the server.
	 */
	public static final String SERVER_DATA_PATH = "TicTacToe_server_settings.xml";
	/**
	 * Database used by server.
	 */
	public static final String SERVER_DATABASE_PATH = "TicTacToe_server_database.xml";
	/**
	 * Saves last used hostname and port for creating server.
	 */
	public static final String SERVER_SERVER_PATH = "TicTacToe_server_hostname.xml";
}
