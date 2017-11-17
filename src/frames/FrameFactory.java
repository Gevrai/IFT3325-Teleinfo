package frames;

import java.util.Arrays;

public class FrameFactory {
	
	public static Frame fromBytes(byte[] frameBytes) throws MalformedFrameException {
		
		if (!isValidFrame(frameBytes))
			throw new MalformedFrameException();
		
		// Instantiates the right type of frame
		switch (frameBytes[0]) {
			case ((byte) 'I') :
				byte num = frameBytes[Frame.TYPE_FIELD_SIZE];
				byte[] data = Arrays.copyOfRange(frameBytes, 
						Frame.TYPE_FIELD_SIZE + Frame.NUM_FIELD_SIZE, 
						frameBytes.length - Frame.CRC_FIELD_SIZE - 1);
				return new InformationFrame(num, data);
			// TODO Implement other types !
			default : 
				throw new MalformedFrameException();
		}
		
	}
	
	// Checks if form is valid as well as CRC
	public static boolean isValidFrame(byte[] framesBytes) {
		// TODO implement this !
		return true;
	}
}
