package frames;

public class AckFrame extends Frame {
	
	public AckFrame(byte frameToAck) {
		super((byte) 'A', frameToAck);
	}

}
