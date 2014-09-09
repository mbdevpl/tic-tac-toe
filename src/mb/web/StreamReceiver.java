package mb.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Used by application to receive input.
 * @author mb
 */
public class StreamReceiver extends BufferedReader {

	public StreamReceiver(InputStream in) {
		super(new InputStreamReader(in));
	}
	
	public String getLine() throws IOException {
		return this.readLine();
	}
}
