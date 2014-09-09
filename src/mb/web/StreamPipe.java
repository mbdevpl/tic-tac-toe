package mb.web;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

/**
 * Pipe of streams that contains two sides, both have inputs and outputs.
 * @author mb
 */
public class StreamPipe {
	public static final char SIDE_A = 'a';
	public static final char SIDE_B = 'b';
	
	private PipedOutputStream pipeBeginA;
	private PipedInputStream pipeEndA;
	private PipedOutputStream pipeBeginB;
	private PipedInputStream pipeEndB;

	private StreamSide streamSideA;
	private StreamSide streamSideB;
	
	private ArrayList<StreamSender> sendersA;
	private ArrayList<StreamReceiver> receiversA;
	private ArrayList<StreamSender> sendersB;
	private ArrayList<StreamReceiver> receiversB;
	
	public StreamPipe() throws IOException {
		pipeBeginA = new PipedOutputStream();
		pipeEndA = new PipedInputStream(pipeBeginA);
		pipeBeginB = new PipedOutputStream();
		pipeEndB = new PipedInputStream(pipeBeginB);
		
		sendersA = new ArrayList<StreamSender>();
		receiversA = new ArrayList<StreamReceiver>();
		sendersB = new ArrayList<StreamSender>();
		receiversB = new ArrayList<StreamReceiver>();
	}
	
	public StreamSide getStreamSide(char side) {
		return side == SIDE_A ? streamSideA : streamSideB;
	}

	public StreamSender addSender(char side) {
		StreamSender sender = new StreamSender(side == SIDE_A ? pipeBeginA : pipeBeginB);
		if(side == SIDE_A)
			sendersA.add(sender);
		else
			sendersB.add(sender);
		return sender;
	}

	public StreamReceiver addReceiver(char side) {
		StreamReceiver receiver = new StreamReceiver(side == SIDE_A ? pipeEndA : pipeEndB);
		if(side == SIDE_A)
			receiversA.add(receiver);
		else
			receiversB.add(receiver);
		return receiver;
	}
	
	public StreamSide getStreamSideA() {
		return getStreamSide(SIDE_A);
	}
	
	public StreamSide getStreamSideB() {
		return getStreamSide(SIDE_B);
	}
	
	public StreamSender addSenderA() {
		return addSender(SIDE_A);
	}

	public StreamReceiver addReceiverA() {
		return addReceiver(SIDE_A);
	}
	
	public StreamSender addSenderB() {
		return addSender(SIDE_B);
	}

	public StreamReceiver addReceiverB() {
		return addReceiver(SIDE_B);
	}
	
	@Override
	public String toString() {
		return "[sendersA=" + sendersA + ", receiversA=" + receiversA
				+ ",\n sendersB=" + sendersB + ", receiversB=" + receiversB + " ]";
	}
}
