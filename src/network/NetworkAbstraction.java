package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import frames.Frame;
import frames.FrameFactory;
import frames.MalformedFrameException;
import utils.BitInputStream;
import utils.BitOutputStream;

/** This class abstracts all of the underlying logic of sending a single frame through and
 *  OutputStream or receiving one from an InputStream.
 *  
 *  Takes care of :
 *  - CRC calculations and verifications
 *  - Bit stuffing and unstuffing
 *  - Frame flagging and unflagging
 * 
 */
public class NetworkAbstraction {

    // Beginning and end flag for frames
    public static final byte flag = (byte) 0b01111110;

    // Binary series corresponding to x^16 + x^12 + x^5 + 1: 10001000000100001
    public static final byte[] GX16 = new byte[] {0b1, 0b00010000, 0b00100001};
    public static final CRCCalculator crcCalculator = new CRCCalculator(GX16);
    
	/** Does everything that should be needed for sending any frame to an outputStream
	 * 
	 * Takes care of CRC creation, bitstuffing and flagging.
	 * 
	 * @param frame Frame to send
	 * @param outputStream Where to send the frame
	 * @throws IOException
	 */
	public static void sendFrame(Frame frame, OutputStream outputStream) throws IOException {
		BitOutputStream ostream = new BitOutputStream(outputStream);
		
		// Adding CRC to the received frame for receiver verification
		byte[] frameBytes = frame.getBytes();
		byte[] crcBytes = crcCalculator.getCRC(frameBytes);
		byte[] allBytes = Arrays.copyOfRange(frameBytes, 0, frameBytes.length + crcBytes.length);
		System.arraycopy(crcBytes, 0, allBytes, frameBytes.length, crcBytes.length);
		
		// Sending start flag
		outputStream.write(flag);

		// Bitstuffing
		int nbOfOnes = 0;
		byte outbit = 0; 
		
		for (byte inbyte : allBytes) {
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
	
	/** Receives the next VALID frame from the outputStream, this function will block until a valid frame is found
	 * 
	 * Takes care of CRC verification, bit unstuffing and unflagging.
	 * 
	 * @param inputStream Where we get the frame from
	 * @return A valid frame
	 * @throws IOException
	 */
	public static Frame receiveFrame(InputStream inputStream) throws IOException, MalformedFrameException {
		BitInputStream istream = new BitInputStream(inputStream);

		ByteArrayOutputStream receivedBytesOStream = new ByteArrayOutputStream();
		BitOutputStream bitOStream = new BitOutputStream(receivedBytesOStream);

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
			
			bitOStream.writeBit(bit);

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
						bitOStream.writeBit(bit);
						bitOStream.writeBit((byte) 0);
						break;
					}
					// Seven 1 in a row should never ever happen ! 
					else throw new MalformedFrameException();
				} 
			}
			
			// Read bit
			bit = istream.readBit();
			bitOStream.writeBit(bit);

			// Count ones
			if (bit == 0) 
				nbOfOnes = 0;
			else 
				nbOfOnes++;
		}
		
		receivedBytesOStream.flush();
		byte[] receivedBytes = receivedBytesOStream.toByteArray();

		// Make sure we got a frame ending with a flag and unflag it
		if(receivedBytes [receivedBytes .length-1] != flag)
			throw new MalformedFrameException();
		receivedBytes  = Arrays.copyOf(receivedBytes , receivedBytes .length-1);
		
		// Check crc
		byte[] crc = crcCalculator.getCRC(receivedBytes );
		for (byte b : crc)
			if (b != 0)
				throw new MalformedFrameException();
		
		// Return valid frame
		return FrameFactory.fromBytes(Arrays.copyOf(receivedBytes , receivedBytes .length-crc.length));
	}

}