package mb.tictactoe.data;

import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * Single message that is in the chat.
 * @author mb
 */
public class ChatMsg extends Xmlable {
	private int id = 0;
	private String type = "CLIENT";
	private String nickname = "";
	private String msg = "";
	
	public ChatMsg(XmlTree tree) {
		id = Integer.parseInt(tree.getAttr("senderid"));
		type = tree.getAttr("sendertype");
		nickname = tree.getAttr("nickname");
		msg = tree.getValue();
	}
	
	public ChatMsg(int id, String type, String nickname, String msg) {
		this.id = id;
		this.type = type;
		this.nickname = nickname;
		this.msg = msg;
	}

	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public String getNickname() {
		return nickname;
	}
	
	public String getMsg() {
		return msg;
	}
	
	@Override
	public String toString() {
		return nickname + ": " + msg;
	}
	
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/chatmsg.dtd");
		tree.setName("chatmsg");
		
		tree.addAttribute("senderid", String.valueOf(getId()));
		tree.addAttribute("sendertype", getType());
		tree.addAttribute("nickname", getNickname());
		
		tree.setValue(getMsg());
		return tree;
	}
}
