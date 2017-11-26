package utils;

import java.io.IOException;
import java.io.OutputStream;

/* This class provides an abstraction of a bit by bit output to a byte stream.
 * 
 * It is not meant to be efficient and one should never use this kind of approach in a real application.
 * The use case of this (eg. bit stuffing) would normally be done in specialized hardware.
 */
public class BitOutputStream {
	
	private OutputStream ostream;
	private byte current;
	private int pos;
	
	public BitOutputStream(OutputStream ostream) {
		this.ostream = ostream;
		this.current = 0;
		this.pos = 0;
	}
	
	public void writeBit(byte outbit) throws IOException {
		// Construct current byte
		if (outbit != 0)
			current = (byte) (current | (0b10000000 >> pos));
		pos++;

		// Write if we have a full byte
		if (pos == Byte.SIZE) {
			ostream.write(current);
			current = 0;
			pos = 0;
		}
	}
	
	public void flush() throws IOException {
		// Was there a byte residue left ?
		if (pos != 0)
			ostream.write(current);
		ostream.flush();
	}

}
