package frames;

import java.util.Arrays;

public class FrameFactory {
	
	public static Frame fromBytes(byte[] frameBytes) throws MalformedFrameException {
		
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
				return new EndConnectionFrame();
			case ('P') :
				return new PFrame();
			default : 
				throw new MalformedFrameException();
		}
	}

}
