package mb.web;

import java.io.InputStream;

/**
 * Receives XML files. 
 * @author mb
 */
public class XmlReceiver extends StreamReceiver {
	
	public XmlReceiver(InputStream in) {
		super(in);
	}
}
