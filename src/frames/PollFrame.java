package frames;

/** Sender asking receiver for a response ASAP.
 *
 */
public class PollFrame extends Frame {
	
	public static final byte TYPE = (byte) 'P';
	
	public PollFrame() {
		super(TYPE, (byte) 0);
	}

}
