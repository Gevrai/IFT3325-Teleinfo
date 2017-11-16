import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import utils.BitInputStream;

class BitInputStreamTest {
	
	private void nextIs(int bit, BitInputStream bis) {
		 try {
			assertEquals((int) bis.readBit(), bit);
		} catch (IOException e) {
			fail("BitInputStream returned an IOException");
		} 
	}

	@Test
	void BytesToBitsTest() throws IOException {
		
		byte[] testBytes = {(byte) 0b00110101, (byte) 0b11110000};
		BitInputStream bis = new BitInputStream(new ByteArrayInputStream(testBytes));
		
		// 00110101
		nextIs(0, bis);
		nextIs(0, bis);
		nextIs(1, bis);
		nextIs(1, bis);
		nextIs(0, bis);
		nextIs(1, bis);
		nextIs(0, bis);
		nextIs(1, bis);

		// 11110000
		nextIs(1, bis);
		nextIs(1, bis);
		nextIs(1, bis);
		nextIs(1, bis);
		nextIs(0, bis);
		nextIs(0, bis);
		nextIs(0, bis);
		nextIs(0, bis);
		
	}

}
