package frames;

public class AckFrame extends Frame {
	
	public static final byte TYPE = (byte) 'A';
	
	public AckFrame(byte frameToAck) {
		super(TYPE, frameToAck);
	}

}
