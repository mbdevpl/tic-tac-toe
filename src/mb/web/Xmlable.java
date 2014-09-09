package mb.web;

/**
 * Superclass of any object that is convertable to XML.
 * @author mb
 */
public abstract class Xmlable {
	
	public abstract XmlTree toXmlTree();
	
	public String toXmlString() {
		return this.toXmlTree().toXmlString();
	}
}
