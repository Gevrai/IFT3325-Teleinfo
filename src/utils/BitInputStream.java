package utils;

import java.io.IOException;
import java.io.InputStream;

/* This class provides an abstraction of a bit by bit reception from a byte stream.
 * 
 * It is not meant to be efficient and one should never use this kind of approach in a real application.
 * This kind of thing would normally be done in specialized hardware.
 */
public class BitInputStream {
	
	InputStream istream;
	byte current;
	int pos;
	
	public BitInputStream(InputStream istream) {
		this.istream = istream;
		this.current = 0;
		this.pos = 0;
	}
	
	// Reads from least to most significant bit in each byte
	public byte read() throws IOException {
		if (pos == 0) {
			current = (byte) istream.read();
			if (current == -1) throw new IOException("Reached end of stream");
		}
		byte bitmask = (byte) (1 << pos);
		byte b = ((current & bitmask) == 0) ? (byte) 0 : (byte) 1;
		pos = (pos+1) % Byte.SIZE;
		return b;
	}

}
