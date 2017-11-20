package frames;

public class ConnectionFrame extends Frame {
	
	public static final byte GO_BACK_N = 0;
	public static final byte SELECTIVE_REJECT = 1;
	public static final byte STOP_AND_WAIT = 2;

	public ConnectionFrame(byte connectionType) {
		super((byte) 'C', connectionType);
	}
        
}
