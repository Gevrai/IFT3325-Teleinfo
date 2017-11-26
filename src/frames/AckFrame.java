package frames;

/** Accepting the 'num' numbered frame received
 *
 */
public class AckFrame extends Frame {
	
	public static final byte TYPE = (byte) 'A';
	
	public AckFrame(byte frameToAck) {
		super(TYPE, frameToAck);
	}

}
