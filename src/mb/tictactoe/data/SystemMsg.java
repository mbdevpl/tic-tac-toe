package mb.tictactoe.data;

import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * System message that contains text and is convertable to XML.
 * @author mb
 */
public class SystemMsg extends Xmlable {
	private String msg;
	
	public SystemMsg(XmlTree tree) {
		msg = tree.getValue();
	}
	
	public SystemMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String toString() {
		return "SystemMsg=[" + msg + "]";
	}
	
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/systemmsg.dtd");
		tree.setName("systemmsg");
		tree.setValue(getMsg());
		return tree;
	}
}
