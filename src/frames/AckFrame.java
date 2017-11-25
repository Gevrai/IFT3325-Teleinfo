package frames;

public class AckFrame extends Frame {
	
	public static byte TYPE = (byte) 'A';
	
	public AckFrame(byte frameToAck) {
		super(TYPE, frameToAck);
	}

}
