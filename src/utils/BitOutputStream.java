package utils;

import java.io.IOException;
import java.io.OutputStream;

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
