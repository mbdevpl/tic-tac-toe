package mb.tictactoe.data;

import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * Data of the user: login, password, player/observer assignment.
 * @author mb
 */
public class Participant extends Xmlable {
	
	public static int ADMIN = 0;
	public static int PLAYER = 1;
	public static int OBSERVER = 2;
	
	//persistant data
	private String nickname = null;
	private String password = null; //@TODO
	private int kind = OBSERVER;
	
	//temporary data
	private int tempId = 0;
	private PlayerSide side = null;
	
	public Participant(String nickname, int kind) {
		if(nickname.length() == 0) throw new IllegalArgumentException("nickname must have length>0");
		this.nickname = nickname;
		this.kind = kind;
		//this.side = new PlayerSide();
	}
	
	public Participant(String nickname, String password, int kind) {
		this(nickname, kind);
		this.password = password;
	}
	
	public Participant(XmlTree tree) {
		nickname = tree.getValue();
		password = tree.getAttr("password");
		kind = Integer.parseInt(tree.getAttr("kind"));
		//side = new PlayerSide();
	}

	public String getName() {
		return new String(nickname);
	}
	
	public int getKind() {
		return kind;
	}
	
	public boolean isAdmin() {
		return kind == ADMIN;
	}
	
	public void setSide(PlayerSide side) {
		this.side = side;
	}
	
	public boolean isPlayer() {
		return kind == PLAYER;
	}
	
	public int getSide() {
		if(side == null) return 0;
		return side.getState();
	}
	
	@Override
	public String toString() {
		return new StringBuffer().append("[name=").append(getName())
				.append(",kind=").append((isPlayer() ? "player" : "observer"))
				.append(",side=").append(side).append("]").toString();
	}
	
	public String toNiceString() {
		StringBuffer s = new StringBuffer().append(getName());
		if(isPlayer())
			s.append(" [player, is ").append(side).append("]");
		else
			s.append(" [observer]");
		return s.append(" id=").append(tempId).append(" ").toString();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Participant) {
			Participant p = (Participant)o;
			if( p.getName().equals(this.getName())
					&& p.isAdmin() == this.isAdmin()
					&& p.isPlayer() == this.isPlayer() )
				return true;
		}
		return false;
	}
	
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/participant.dtd");
		tree.setName("participant");
		tree.setValue(nickname);
		tree.addAttribute("password", password);
		tree.addAttribute("kind", String.valueOf(kind));
		
		return tree;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setTempId(int tempId) {
		this.tempId = tempId;
	}

	public int getTempId() {
		return tempId;
	}
	
	public char getSymbol() {
		if(side == null) return ' ';
		return side.getSymbol();
	}
}
