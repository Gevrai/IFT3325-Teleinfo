package frames;

/** Receiver rejecting the 'num' frame (did not receive or invalid)
 *
 */
public class RejFrame extends Frame {
	
	public static final byte TYPE = (byte) 'R';
	
	public RejFrame(byte frameToRej) {
		super(TYPE, frameToRej);
	}

}
