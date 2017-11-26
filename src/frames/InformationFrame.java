package frames;

/** Data sending frame with 'num' field to identifiate it.
 *
 */
public class InformationFrame extends Frame {

	public static final byte TYPE = (byte) 'I';
	
	public InformationFrame(byte frameNumber, byte[] data) {
		super(TYPE, frameNumber, data);
	}

}
