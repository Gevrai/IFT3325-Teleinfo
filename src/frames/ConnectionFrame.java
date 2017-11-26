package frames;

public class ConnectionFrame extends Frame {

	public static final byte TYPE = (byte) 'C';
	
	public static final byte GO_BACK_N = 0;
	public static final byte SELECTIVE_REJECT = 1;
	public static final byte STOP_AND_WAIT = 2;

	public ConnectionFrame(byte connectionType) {
		super(TYPE, connectionType);
	}

	public static boolean isValidConnectionType(byte ct) {
		switch (ct) {
		case ConnectionFrame.GO_BACK_N :
		case ConnectionFrame.SELECTIVE_REJECT :
		case ConnectionFrame.STOP_AND_WAIT :
			return true;
		default :
			return false;
		}
	}
}
