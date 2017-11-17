package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import frames.Frame;
import frames.FrameFactory;
import frames.InformationFrame;
import frames.MalformedFrameException;

public class NetworkAbstraction {

    public static final byte flag = (byte) 0b01111110;
    
	/** Does everything that should be needed for sending any frame to a socket
	 * 
	 * Takes care of bit stuffing and flagging
	 * 
	 * @param frame Frame to send
	 * @param outputStream Where to send the frame
	 * @throws IOException
	 */
	public static void sendFrame(Frame frame, OutputStream outputStream) throws IOException {
		BitOutputStream ostream = new BitOutputStream(outputStream);
		byte[] frameBytes = frame.getBytes();
		
		// Start flag
		outputStream.write(flag);

		// Bitstuffing
		int nbOfOnes = 0;
		byte outbit = 0; 
		
		for (byte inbyte : frameBytes) {
			for (int inpos = 0; inpos < Byte.SIZE ; inpos ++) {
				// Next bit
				outbit = ((0b10000000 >>> inpos) & inbyte) == 0 ? (byte) 0 : (byte) 1;
				
				// If a 1 add to counter
				if (outbit == 1) 
					nbOfOnes++;
				else 
					nbOfOnes = 0;

				// Write current bit to output stream
				ostream.writeBit(outbit);
				
				// If the last 5 bits where 1s, add a zero
				if (nbOfOnes == 5) {
					ostream.writeBit((byte) 0);
					nbOfOnes = 0;
				}
			}
		}
		
		// End flag, might not be aligned with full byte
		for (int i=0;i<Byte.SIZE;i++) {
			ostream.writeBit((byte) (flag & (0b10000000 >>> i)));
		}
		
		// Flush for safety !
		ostream.flush();
	}
	
	/** Receives the next VALID frame from the socket, this function will block until a valid frame is found
	 * 
	 * Takes care of bit unstuffing and unflagging
	 * 
	 * @param inputStream Where we get the frame from
	 * @return A valid frame
	 * @throws IOException
	 */
	public static Frame receiveFrame(InputStream inputStream) throws IOException, MalformedFrameException {
		BitInputStream istream = new BitInputStream(inputStream);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BitOutputStream ostream = new BitOutputStream(bytes);

		int nbOfOnes = 0;
		byte bit = 0;
		
		// Start flag, assumes that every frame's beginning is aligned with a byte
		byte current = (byte) inputStream.read();
		while (current != flag) 
			current = (byte) inputStream.read();
		// Make sure current is not a flag
		while (current == flag) 
			current = (byte) inputStream.read();
		
		// Still need to read the current byte... This is not very clean though
		for (int i=0; i<Byte.SIZE; i++) {
			bit = (current & (0b10000000 >>> i)) == 0? (byte) 0: (byte) 1;
			if (bit == 1) 
				nbOfOnes++;
			else 
				nbOfOnes = 0;
			
			ostream.writeBit(bit);

			// If we have 5 ones and it is not the last bit, ignore next 0
			if(nbOfOnes == 5 && i+1 != Byte.SIZE) {
				i++;
				bit = (current & (0b10000000 >>> i)) == 0? (byte) 0: (byte) 1;
				// If the next bit is not a zero, we have a problem...
				if (bit != 0)
					throw new MalformedFrameException();
				nbOfOnes = 0;
			}
		}
		
		// Main loop, breaks only if we find a flag
		while (true) {
			
			// Suites of five 1 are either a flag or bit stuffing happened
			if (nbOfOnes == 5) {
				// Drop next zero
				if ((bit = istream.readBit()) == 0) {
					nbOfOnes = 0;
				} 
				else /* (bit == 1) */ {
					// Found a flag, finish it and break!
					if (istream.readBit() == 0) {
						ostream.writeBit(bit);
						ostream.writeBit((byte) 0);
						break;
					}
					// Seven 1 in a row should never ever happen ! 
					else throw new MalformedFrameException();
				} 
			}
			
			// Read bit
			bit = istream.readBit();
			ostream.writeBit(bit);

			// Count ones
			if (bit == 0) 
				nbOfOnes = 0;
			else 
				nbOfOnes++;
		}
		
		// Readying up the array !
		bytes.flush();
		byte[] resultFlagged = bytes.toByteArray();
		// Make sure we got a frame ending with a flag
		if(resultFlagged[resultFlagged.length-1] != flag)
			throw new MalformedFrameException();
		
		// Unflag it and make a new frame !
		byte[] result = Arrays.copyOfRange(resultFlagged, 0, resultFlagged.length - 1);
		return FrameFactory.fromBytes(result);
	}
	
	// Only purpose is for manual testing, should be getting rid of it after we make some unit tests
	public static void main(String args[]) throws IOException {
		// 011110111111111100000000
		byte[] testData = {(byte) 0b01111011, (byte) 0xFF, (byte) 0x00};
		Frame f = new InformationFrame((byte) 0, testData);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		
		sendFrame(f, ostream);
		
		byte[] original = f.getBytes();
		byte[] result = ostream.toByteArray();
		String originalStr = "        ";
		String resultStr = "";
		
		for(int i=0; i<original.length;i++)
			for(int j=0; j<Byte.SIZE;j++) {
				originalStr += (original[i] & (0b10000000 >>> j)) == 0? '0':'1';
			}

		for(int i=0; i<result.length;i++)
			for(int j=0; j<Byte.SIZE;j++) {
				resultStr += (result[i] & (0b10000000 >>> j)) == 0? '0':'1';
			}
		
		System.out.println("Original : " + originalStr);
		System.out.println("Result   : " + resultStr);
		
	}
}
