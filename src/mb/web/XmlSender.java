package mb.web;

import java.io.OutputStream;

/**
 * Sends XML messages.
 * @author mb
 */
public class XmlSender extends StreamSender {
	
	public XmlSender(OutputStream out) {
		super(out);
	}
	
	public static String createHeader(String name) {
		return "<?xml version=\"1.0\" encoding=\"iso-8859-2\"?>"
				+ "<!DOCTYPE " + name + " SYSTEM \"http://mbdev.pl/files/tictactoe/" + name + ".dtd\">";
	}
}
