package frames;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FrameBuilder {
	
	private InputStream istream;
	
	public FrameBuilder(InputStream istream) {
		this.istream = istream;
	}
	
	public Frame readNextFrame() throws MalformedFrameException {
		try {
			List<Byte> bytes = new ArrayList<Byte>();
			byte current, next;
			
			// Check flag is present for beginning of frame
			if ((current = (byte) istream.read()) != Frame.flag) {
				// If not read, drop the rest of the previous frame
				while ((current = (byte) istream.read()) != Frame.flag) {}
				if ((current = (byte) istream.read()) != Frame.flag) {
				}

			// This should be the flag byte in all cases
			bytes.add(current);
			
			// Read information between flags
			while ( (current = (byte) istream.read()) != Frame.flag ) {
				bytes.add(current);
			}
			
			// End flag

			
		} catch (IOException e) {
			
		}
		
	}
	

}
