package frames;

import java.util.Arrays;

public class FrameFactory {
	
	/* Factory to instanciate the correct frame type (with verifications) from a received 
	 * byte array. This should only be used by NetworkAbstraction
	 */
	public static Frame fromBytes(byte[] frameBytes) throws MalformedFrameException {
		
		// Illegal frameBytes
		if (frameBytes == null || frameBytes.length < 2)
			throw new MalformedFrameException();
		
		// Instantiates the right type of frame
		char type = (char) frameBytes[0];
		switch (type) {
			case ('I') :
				byte num = frameBytes[Frame.TYPE_FIELD_SIZE];
				byte[] data = Arrays.copyOfRange(frameBytes, Frame.TYPE_FIELD_SIZE + Frame.NUM_FIELD_SIZE, frameBytes.length);
				return new InformationFrame(num, data);
			case ('C') :
				return new ConnectionFrame(frameBytes[Frame.TYPE_FIELD_SIZE]);
			case ('A') :
				return new AckFrame(frameBytes[Frame.TYPE_FIELD_SIZE]);
			case ('R') :
				return new RejFrame(frameBytes[Frame.TYPE_FIELD_SIZE]);
			case ('F') :
				return new FinalFrame();
			case ('P') :
				return new PollFrame();
			default : 
				throw new MalformedFrameException();
		}
	}

}
