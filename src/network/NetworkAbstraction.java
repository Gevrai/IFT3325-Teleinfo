package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import frames.Frame;
import frames.FrameFactory;
import frames.MalformedFrameException;
import utils.BitInputStream;
import utils.BitOutputStream;
import utils.Log;

/** This class abstracts all of the underlying logic of sending and receiving single frames
 *  in a socket. It is possible to initialize this network with a communication error ratio.
 *
 *  Takes care of :
 *  - CRC calculations and verifications
 *  - Bit stuffing and unstuffing
 *  - Frame flagging and unflagging
 *  - Introducing errors on communication
 *  
 *  The format of data sent and received over this network is as follow :
 *  
 *  | 1 Byte |    X Byte(s)   | 2 Bytes | 1 Byte |
 *  |--------|----------------|---------|--------|
 *  |  Flag  |     Frame      |   CRC   |  Flag  |
 *  |--------|----------------|---------|--------|
 *
 */
public class NetworkAbstraction {

	// Beginning and end flag for frames
	public static final byte flag = (byte) 0b01111110;

	// Binary series corresponding to x^16 + x^12 + x^5 + 1: 10001000000100001
	public static final byte[] GX16 = new byte[] {0b1, 0b00010000, 0b00100001};
	public static final CRCCalculator crcCalculator = new CRCCalculator(GX16);
	
	// Communication error ratio
	private ErrorGenerator errorGenerator;

	// Socket on which the communication is done
	private Socket socket;

	// Perfect communication network
	public NetworkAbstraction(Socket socket) throws IOException {
		this.socket = socket;
		this.errorGenerator = new ErrorGenerator(0.0);
	}

	// Potentially imperfect communication network
	public NetworkAbstraction(Socket socket, double errorRatio) throws IOException {
		this.socket = socket;
		this.errorGenerator = new ErrorGenerator(errorRatio);
	}
	
	// Get the name of host attached to socket
	public String getHostName() {
		return socket.getLocalAddress().getHostName();
	}
	
	// Simply closes the socket
	public void close() throws IOException {
		this.socket.close();
	}

	/** Does everything that should be needed for sending any frame to an outputStream
	 *
	 * Takes care of CRC creation, bitstuffing and flagging.
	 *
	 * @param frame Frame to send
	 * @param outputStream Where to send the frame
	 * @throws IOException
	 */
	public void sendFrame(Frame frame) throws IOException {
		ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
		BitOutputStream ostream = new BitOutputStream(byteOStream);

		// Adding CRC to the received frame for receiver verification
		byte[] frameBytes = frame.getBytes();
		byte[] crcBytes = crcCalculator.getCRC(frameBytes);
		byte[] allBytes = Arrays.copyOfRange(frameBytes, 0, frameBytes.length + crcBytes.length);
		System.arraycopy(crcBytes, 0, allBytes, frameBytes.length, crcBytes.length);

		// Sending start flag
		this.socket.getOutputStream().write(flag);

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
		
		// Maybe introduce errors on this communication
		byte[] bytesToSend = errorGenerator.apply(byteOStream.toByteArray());
		
		// Send the data
		this.socket.getOutputStream().write(bytesToSend);
	}

	/** Receives the next VALID frame from the outputStream, this function will block until a valid frame is found
	 *
	 * Takes care of CRC verification, bit unstuffing and unflagging.
	 *
	 * @param inputStream Where we get the frame from
	 * @return A valid frame
	 * @throws IOException
	 */
	public Frame receiveFrame() throws IOException {
		// Will block until we get a valid frame !
		while (true) {
			try {
				byte[] receivedBytes = getNextUnstuffedBytesBetweenFlags();
				// Check crc
				byte[] crc = crcCalculator.getCRC(receivedBytes);
				for (byte b : crc)
					if (b != 0) {
						throw new MalformedFrameException();
					}
				// Return valid frame
				return FrameFactory.fromBytes(Arrays.copyOf(receivedBytes , receivedBytes .length-crc.length));
			}
			// If there was a problem with the frame, drop it and continue
			catch (MalformedFrameException e) {
				Log.verbose("Received invalid frame... Dropping...");
			}
		}
	}

	// Get bytes from the socket, unflag and unstuffs it and return a byte array or a MalformedFrameException
	private byte[] getNextUnstuffedBytesBetweenFlags() throws IOException, MalformedFrameException {
		InputStream istream = this.socket.getInputStream();
		BitInputStream bitInputStream = new BitInputStream(istream);

		ByteArrayOutputStream receivedBytesOStream = new ByteArrayOutputStream();
		BitOutputStream bitOuputStream = new BitOutputStream(receivedBytesOStream);

		int nbOfOnes = 0;
		byte bit = 0;

		// Start flag, assumes that every frame's beginning is aligned with a byte
		byte current = (byte) istream.read();
		while (current != flag)
			current = (byte) istream.read();
		// Make sure current is not a flag
		while (current == flag)
			current = (byte) istream.read();

		// Still need to read the current byte... This is not very clean though
		for (int i=0; i<Byte.SIZE; i++) {
			bit = (current & (0b10000000 >>> i)) == 0? (byte) 0: (byte) 1;
			if (bit == 1)
				nbOfOnes++;
			else
				nbOfOnes = 0;

			bitOuputStream.writeBit(bit);

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
				if ((bit = bitInputStream.readBit()) == 0) {
					nbOfOnes = 0;
				} else /* (bit == 1) */ {
					// Found a flag, finish it and break!
					if (bitInputStream.readBit() == 0) {
						bitOuputStream.writeBit(bit);
						bitOuputStream.writeBit((byte) 0);
						break;
					}
					// Seven 1 in a row should never ever happen !
					else throw new MalformedFrameException();
				}
			}

			// Read bit
			bit = bitInputStream.readBit();
			bitOuputStream.writeBit(bit);

			// Count ones
			if (bit == 0)
				nbOfOnes = 0;
			else
				nbOfOnes++;
		}

		receivedBytesOStream.flush();
		
		// Maybe introduce errors on reception...
		byte[] receivedBytes = errorGenerator.apply(receivedBytesOStream.toByteArray());

		// Make sure we got a frame ending with a flag and unflag it
		if(receivedBytes [receivedBytes .length-1] != flag)
			throw new MalformedFrameException();
		return Arrays.copyOf(receivedBytes , receivedBytes .length-1);
	}
	

}
