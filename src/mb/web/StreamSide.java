package mb.web;

/**
 * One side of the stream, contains input and output, and when connected to another side, input
 * connects with output on the other side, and output connects to input from other side.
 * @author mb
 *
 */
public class StreamSide {
	private StreamPipe pipe;
	private char side;

	private StreamSender sender; 
	private StreamReceiver receiver;
	
	public StreamSide(StreamPipe pipe, char side) {
		this.pipe = pipe;
		this.side = side;
		
		receiver = pipe.addReceiver(side);
		sender = pipe.addSender(side);
	}
	
	public StreamSender getSender() {
		return sender;
	}
	
	public StreamReceiver getReceiver() {
		return receiver;
	}
	
	public StreamSender addSender() {
		return pipe.addSender(side);
	}
	
	public StreamReceiver addReceiver() {
		return pipe.addReceiver(side);
	}
}
