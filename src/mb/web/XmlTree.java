package mb.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Representation of XML message with use of tree, where single node can have dtd, name, value, unlimited attributes
 * and unlimited children, of which every is also a node with the same properties.
 * @author mb
 */
public class XmlTree {
	private String dtdURL;
	private String name;
	private Hashtable<String, String> attr; //attributes list
	private String value;
	private Hashtable<String, XmlTree> subNodes;
	
	public XmlTree() {
		dtdURL = "";
		name = "";
		attr = new Hashtable<String, String>();
		value = "";
		subNodes = new Hashtable<String, XmlTree>();
	}
	
	public XmlTree(XmlTree tree) {
		System.out.println("xml copy constructor");
		this.dtdURL = tree.getDtdURL();
		this.name = tree.getName();
		this.value = tree.getValue();
		this.subNodes = tree.subNodes;
		this.attr = tree.attr;
	}
	
	/**
	 * Creates a tree representing the XML file.
	 * @param xmlStr string containing the whole XML file
	 */
	public XmlTree(String xmlStr) {
		this();
		
		boolean printInfo = false;
		
		try {
		//simple check of header
		if(xmlStr.indexOf("<?xml ") == 0) {
			
			//URL of document type (DTD) 
			int doctypeBegPos = xmlStr.indexOf("!DOCTYPE"),
				doctypeEndPos = xmlStr.indexOf('>', doctypeBegPos);
			dtdURL = xmlStr.substring(xmlStr.indexOf('"', doctypeBegPos) + 1,
					xmlStr.lastIndexOf('"', doctypeEndPos));
			xmlStr = xmlStr.substring(doctypeEndPos + 1);
			
		} else dtdURL = "";
		
		//System.out.println(xmlStr);
		if(printInfo) System.out.println("  got DTD = " + dtdURL);
		
		//name of the node
		int nameEndPos = -1;
		if(xmlStr.indexOf(' ') > xmlStr.indexOf('>') || xmlStr.indexOf(' ') == -1)
			nameEndPos = xmlStr.indexOf('>');
		else nameEndPos = xmlStr.indexOf(' ');
		name = xmlStr.substring(1, nameEndPos);
		
		if(printInfo) System.out.println("  got NAME = " + name);
		
		//attributes
		if(xmlStr.charAt(nameEndPos) != '>') {
			String attrStr = xmlStr.substring(nameEndPos + 1, xmlStr.indexOf('>', nameEndPos + 1));
			//attrStr = attrStr.substring(attrStr.indexOf(' ') + 1);
			while(attrStr.indexOf('"') != -1) {
				int splitIndex = attrStr.indexOf("=\"");
				int endIndex = 0;
				if(attrStr.indexOf("\" ") == -1) endIndex = attrStr.lastIndexOf('"');
				else {
					if(attrStr.contains("\" \"")) endIndex = attrStr.indexOf("\" " , splitIndex + 2);
					else endIndex = attrStr.indexOf("\" ");
				}
				String key = attrStr.substring(0, splitIndex);
				String value = attrStr.substring(splitIndex + 2, endIndex);
				attr.put(key, value);
				
				if(printInfo) System.out.println("  got ATTR [" + key + "=" + value + "]");
				
				if(attrStr.length() > endIndex + 2)
					attrStr = attrStr.substring(endIndex + 2);
				else attrStr = "";
			}
			
			if(printInfo) System.out.println("  got all ATTR");
		} else if(printInfo) System.out.println("  got ZERO ATTR");
		
		//sub-nodes OR value of the node
		if(xmlStr.indexOf('>') < xmlStr.lastIndexOf('<')) {
			int subNodesBegin = xmlStr.indexOf('>') + 1;
			int subNodesEnd = xmlStr.indexOf("</" + name);
			String subStr = xmlStr.substring(subNodesBegin, subNodesEnd);
			if(subStr.indexOf('<') != -1) {
				if(printInfo) System.out.println("xml=" + xmlStr + ";");
				if(printInfo) System.out.println("sub=" + subStr + ";");
				do {
					//System.out.println("Subnodes are not implemented!"); // they ARE now :)
					
					int nameEnd = 0;
					if(subStr.indexOf(' ') == -1 || subStr.indexOf('>') < subStr.indexOf(' '))
						nameEnd = subStr.indexOf('>');
					else
						nameEnd = subStr.indexOf(' ');
					
					String name = subStr.substring(1, nameEnd);
					
					int currSubNodeEnd = 0;
					if(subStr.indexOf('>') - 1 == subStr.indexOf("/>"))
						currSubNodeEnd = subStr.indexOf("/>") + 2;
					else
						currSubNodeEnd = subStr.indexOf("</" + name) + 2 + name.length() + 1;
					
					int subscript = 0;
					String suffix = "";
					do {
						suffix = String.valueOf(subscript);
						for(int i = suffix.length(); i < 5; i++) suffix = "0" + suffix;
						subscript++;
					} while(subNodes.get(name + suffix) != null);
					
					if(printInfo) System.out.println(" currSub=" + subStr.substring(0, currSubNodeEnd) + ";");
					XmlTree subTree = new XmlTree(subStr.substring(0, currSubNodeEnd));
					subNodes.put(name + suffix, subTree);
					subStr = subStr.substring(currSubNodeEnd);
					
				} while(subStr.indexOf('<') != -1);
				
				if(printInfo) System.out.println("  got all SUBN = " + subNodes.keySet());
			} else {
				value = subStr;
				if(printInfo) System.out.println("  got VALUE = " + value);
			}
		} else if(printInfo) System.out.println("  got ZERO SUBN");
		
		} catch (Exception e) {
			String commentary = xmlStr + ",";
			if(name != null)
				commentary += "name=" + name + ",";
			if(dtdURL != null)
				commentary += "dtd=" + dtdURL + ",";
			if(attr != null)
				commentary += "attr=" + attr.toString() + ",";
			if(value != null)
				commentary += "value=" + value + ",";
			//if(subNodes != null)
			//	commentary += "subNodes=" + subNodes.toString();
			//if(commentary.equals(""))
			//	commentary = xmlStr;
			//if(commentary.endsWith(","))
			//	commentary += "...";
			throw new IllegalArgumentException("Error while parsing xml! ("
					+ e.getClass().getSimpleName() + ": " + e.getMessage() + " and " + commentary + ")");
		}
		
		//if(name.equals("row"))
		//	System.out.println("GAMEROW CONSTR. " + this.toString());
	}
	
	/**
	 * Creates a tree that represents this object. Object must be one of the objects implemented by this class.
	 * @param o object for conversion
	 */
	public XmlTree(Xmlable o) {
		this(o.toXmlTree());
	}

	public String getDtdURL() {
		return dtdURL;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAttr(String attrName) {
//		attr.
//		for(Iterator<String> it = attr.keySet().iterator(); it != null) {
//			if(it.next().equals(attrName))
//				return
//		}
		return attr.get(attrName);
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * Gets a sub node with a given subscript.
	 * @param nodeName
	 * @param subscript
	 * @return
	 */
	public XmlTree getSubNode(String nodeName, int subscript) {
		//System.out.println(this);
//		int j = 0;
//		for(XmlTree t: subNodes.values()) {
//			if(t.name.equals(nodeName)) {
//				j++;
//				if(j > i) return t;
//			}
//		}
		String s = String.valueOf(subscript);
		for(int i = s.length(); i < 5; i++)
			s = "0" + s;
		
		XmlTree node = subNodes.get(nodeName + s);
		if(node == null) throw new NullPointerException("could not find subnode with name: " + nodeName + s);
		return node;
	}
	
	public String toString() {
		return "[ name = " + getName() + ",\n attributes=" + attr.keySet()
				+ ",\n attr.values=" + attr.values().toString()
				+ ",\n value=" + getValue()
				+ ",\n sub-nodes=" + subNodes.keySet() + " ]";
	}

	public String toXmlString() {
		String str = "";
		if(!dtdURL.equals(""))
			str += XmlSender.createHeader(name);
		str += "<" + name;
		if(attr.size() > 0) {
			Enumeration<String> e = attr.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				str += " " + key + "=\"" + attr.get(key) + "\"";
			}
		}
		if(value.length() > 0)
			str += ">" + value + "</" + name + ">";
		
		else if(subNodes.size() > 0) {
			/*str += ">";
			Enumeration<String> e = ;
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				str += subNodes.get(key).toXmlString();
			}
			str += "</" + name + ">";*/
			
			str += ">";
			
			ArrayList<String> nodes = Collections.list(subNodes.keys());
			Collections.sort(nodes);
			for(String key: nodes) {
				str += subNodes.get(key).toXmlString();
			}
			
			str += "</" + name + ">";
		}
		else str += " />";
		return str;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDtdURL(String dtdURL) {
		this.dtdURL = dtdURL;
	}
	
	public void addAttribute(String name, String value) {
		this.attr.put(name, value);
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Adds new subnode with the specific subscript.
	 * @param node
	 * @param subscript
	 */
	public void addSubNode(XmlTree node, int subscript) {
		String sub = String.valueOf(subscript);
		for(int i = sub.length(); i < 5; i++)
			sub = "0" + sub;
		
		this.subNodes.put(node.getName() + sub, node);
	}
	
	/**
	 * Adds new sub node at the first free index.
	 * @param node sub-node
	 */
	public void addSubNode(XmlTree node) {
		int subscript = 0;
		while(true) {
			String name = String.valueOf(subscript);
			for(int i = name.length(); i < 5; i++)
				name = "0" + name;
			name = node.getName() + name;
			
			if(subNodes.get(name) == null) {
				this.subNodes.put(name, node);
				break;
			}
			subscript++;
		} //while(this.subNodes.get(node.getName() + s).equals(node));
	}
	
	public static String createXmlString(Xmlable o) {
		XmlTree tree = new XmlTree(o);
		return tree.toXmlString();
	}
	
	/*public static Object createObject(String xmlString) {
		XmlTree tree = new XmlTree(xmlString);
		return tree.toObject();
	}*/

	
	public void saveToFile(String filePath) {
		StreamSender out;
		try {
			out = new StreamSender(new FileOutputStream(filePath));
		} catch (FileNotFoundException fe) {
			throw new NullPointerException("Xml file cannot be created.");
		}

		String treeStr = this.toXmlString();
		treeStr = treeStr.replace(">", ">\n");
		out.write(treeStr);
		out.flush();
		out.close();
	}
	
	public static XmlTree createFromFile(String filePath) throws FileNotFoundException, IOException {
		StreamReceiver in = new StreamReceiver(new FileInputStream(filePath));
		
		String treeStr = new String();
		while(in.ready()) {
			treeStr = treeStr.concat(in.readLine());
		}
		//treeStr = treeStr.replace(">", ">\n");
		
		return new XmlTree(treeStr);
	}
	
	public void removeAttr(String name) {
		attr.remove(name);
	}


	private String toFullString(int indent, String prefix) {
		String s = "";
		for(int i = 0; i < indent; i++) s += " ";
		
		s += prefix + "=[name=" + getName();
		if(attr.size() > 0) s += ",attr= " + attr.entrySet() + " ";
		if(value.length() > 0) s += ",value=" + getValue();
		
		if(subNodes.size() > 0) {
			for(String sub: Collections.list(subNodes.keys())) {
				s += "\n";
				s += subNodes.get(sub).toFullString(indent + 2, sub);
			}
			s += "\n";
			for(int i = 0; i < indent; i++) s += " ";
		}
		s += "]";
		return s;
	}
	
	/**
	 * Prints out the contents of the tree, recursively. 
	 * @return
	 */
	public String toFullString() {
		return toFullString(0, "");
	}
}
