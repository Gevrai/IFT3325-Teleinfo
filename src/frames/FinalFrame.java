package frames;

/** Frame sent from sender to receiver to ask for disconnection.
 *
 */
public class FinalFrame extends Frame {

	public static final byte TYPE = (byte) 'F';
	
	public FinalFrame() {
		super(TYPE, (byte) 0);
	}

}
