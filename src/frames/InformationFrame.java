package frames;

public class InformationFrame extends Frame {
	
	public InformationFrame(byte frameNumber, byte[] data) {
		super((byte) 'I', frameNumber, data);
	}

}
