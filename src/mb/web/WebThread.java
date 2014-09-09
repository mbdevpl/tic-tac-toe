package mb.web;

/**
 * Extension of Thread class, that stores basic data used for networking. 
 * @author mb
 */
public abstract class WebThread extends Thread {
	public static final String defaultHostname = "localhost";
	public static final int defaultPort = 16785;
	public static final int defaultTimeout = 1000;
	public static final int deafultMaxTimeoutCount = 5;
	
	protected static final int LISTENING = 1; //idle state
	protected static final int WAITING = 2; //waiting for a relatively quick response, disconnects if none received

	protected int maxTimeoutCount = deafultMaxTimeoutCount;
	protected int state = LISTENING;
	protected int timeoutCounter = 0;
	private volatile boolean stopMe = false;
	
	private String suffix;
	private volatile int webId;
	private volatile String hostname = defaultHostname;
	private volatile int port = defaultPort;
	private volatile int timeout = defaultTimeout;
	
	public WebThread(String suffix, String hostname, int port, int timeout) {
		super("TicTacToe-" + suffix);
		this.webId = -1;
		this.suffix = suffix;
		this.hostname = hostname;
		this.port = port;
		this.timeout = timeout;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public int getWebId() {
		return webId;
	}
	
	public void setWebId(int webId) {
		this.webId = webId;
	}
	
	/**
	 * Adds an entry to log. What a log is will probably change over time.
	 * The direction of output of different kinds of threads may differ,
	 * but all of them must implement a log. Implementation may look
	 * like "System.out.println(s);" but it must be done.
	 * @param s an entry
	 */
	public void log(String s) {
		System.out.println(this + ": " + s); //default log output
	}
	
	public boolean stopMe() {
		if(stopMe) return false;
		stopMe = true;
		return true;
	}
	
	@Override
	public String toString() {
		return "WebThread:" + suffix + "-" + webId;
	}
	
	@Override
	public void run() {
		while(!stopMe) {
			//log("...");
			try {
				doActions();
			} catch(RuntimeException e) {
				//if(!e.getMessage().equals(""))
					log("exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					//e.printStackTrace();
			}
		}
		log("DEATH");
	}
	
	protected abstract void doActions();
}
