package mb.web;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Used by application to send output.
 * @author mb
 */
public class StreamSender extends PrintWriter {

	public StreamSender(OutputStream out) {
		super(out);
	}
	
	public void send(String s) {
		this.write(s + "\n");
		this.flush();
	}
}
