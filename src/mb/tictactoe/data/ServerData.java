package mb.tictactoe.data;

import java.util.Enumeration;
import java.util.Hashtable;

import mb.web.XmlSender;
import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * Database of the server, convertable to XML.
 * @author mb
 */
public class ServerData extends Xmlable {
	class UserData {
		public String password;
		public int gamesPlayed, gamesWon;
		
		public UserData(String password, int gamesPlayed, int gamesWon) {
			this.password = password;
			this.gamesPlayed = gamesPlayed;
			this.gamesWon = gamesWon;
		}
		
		public UserData() {
			this.password = "";
			this.gamesPlayed = 0;
			this.gamesWon = 0;
		}
	}
	
	Hashtable<String, UserData> users;
	
	public ServerData(XmlTree tree) {
		this();
		int i = 0;
		XmlTree userTree = null;
		do {
			try {
				userTree = tree.getSubNode("user", i++);
			} catch(Exception e) {
				break;
			}
			String login = userTree.getValue();
			UserData u = new UserData();
			u.password = userTree.getAttr("password");
			u.gamesPlayed = Integer.parseInt(userTree.getAttr("gamesplayed"));
			u.gamesWon = Integer.parseInt(userTree.getAttr("gameswon"));
			
			users.put(login, u);
			
		} while(userTree != null);
	}
	
	public ServerData() {
		users = new Hashtable<String, UserData>();
	}

	@Override
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		
		tree.setDtdURL(XmlSender.createHeader("serverdata"));
		tree.setName("serverdata");
		
		Enumeration<String> enu = users.keys();
		for(int i = 0; enu.hasMoreElements() ; i++) {
			String currLogin = enu.nextElement();
			UserData u = users.get(currLogin);
			
			XmlTree sub = new XmlTree();
			sub.setName("user");
			sub.setValue(currLogin);
			sub.addAttribute("password", u.password);
			sub.addAttribute("gamesplayed", String.valueOf(u.gamesPlayed));
			sub.addAttribute("gameswon", String.valueOf(u.gamesWon));
			tree.addSubNode(sub);
		}
		
		return tree;
	}
	
	public synchronized boolean credentialsOk(String login, String password) {
		UserData u = users.get(login);
		if(u == null || !u.password.equals(password))
			return false;
		return true;
	}
	
	public synchronized boolean containsAccount(Participant p) {
		if(users.get(p.getName()) == null)
			return false;
		else return true;
	}
	
	public boolean addAccount(String login, String password, int gamesPlayed, int gamesWon) {
		if(users.get(login) != null) return false;
		users.put(login, new UserData(password, gamesPlayed, gamesWon));
		return true;
	}
	
	public synchronized void increaseGamesWon(Participant p) {
		UserData ud = users.get(p.getName());
		ud.gamesWon = ud.gamesWon + 1;
		users.remove(p.getName());
		users.put(p.getName(), ud);
	}
	
	public synchronized void increaseGamesPlayed(Participant p) {
		UserData ud = users.get(p.getName());
		ud.gamesPlayed = ud.gamesPlayed + 1;
		users.remove(p.getName());
		users.put(p.getName(), ud);
	}
	
	public boolean removeAccount(Participant p) {
		String s = p.getName();
		UserData u = users.get(s);
		if(u != null &&u.password.equals(p.getPassword())) {
			users.remove(s);
			return true;
		}
		return false;
	}

	
	public String getWinCount(Participant p) {
		UserData u = users.get(p.getName());
		return String.valueOf(u.gamesWon);
	}

	public String getPlayCount(Participant p) {
		UserData u = users.get(p.getName());
		return String.valueOf(u.gamesPlayed);
	}
	
}
