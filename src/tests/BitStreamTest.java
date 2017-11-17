package tests;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import utils.BitInputStream;
import utils.BitOutputStream;

public class BitStreamTest {

	// Probably overkill, this makes for very exhaustive testing, but it is still fast (<30ms)
	private static final int BYTE_ARRAY_SIZE = 2^Byte.SIZE;
	private static byte[] TEST_BYTES = new byte[BYTE_ARRAY_SIZE];
	
	@BeforeClass
	public static void byteArraySetup() {
		TEST_BYTES = new byte[BYTE_ARRAY_SIZE];
		for (int i=0; i<BYTE_ARRAY_SIZE;i++)
			TEST_BYTES[i] = (byte) i;
	}
	
	@Test
	public void BitInputStreamTest() throws IOException {
		BitInputStream istream = new BitInputStream(new ByteArrayInputStream(TEST_BYTES));

		for (int i=0; i < TEST_BYTES.length; i++) {
			for (int j=0; j<Byte.SIZE;j++) {
				byte current = (TEST_BYTES[i] & (0b10000000 >>> j)) == 0? (byte)0:(byte)1;
				assertEquals(current, (int) istream.readBit());
			}
		}
	}
	
	@Test
	public void BitOutputStreamTest() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitOutputStream ostream = new BitOutputStream(byteArrayOutputStream);
		
		// Write the bytes to the output stream
		for (int i=0; i < TEST_BYTES.length; i++) {
			for (int j=0; j<Byte.SIZE;j++) {
				ostream.writeBit((byte) (TEST_BYTES[i] & (0b10000000 >>> j)));
			}
		}
		
		// ByteArrayOutputStream gives us a neat little function to get back what we wrote !
		byte[] bytesResult = byteArrayOutputStream.toByteArray();
		
		// Test equality between source testBytes and byteArrayOutputStream
		assertEquals(bytesResult.length, TEST_BYTES.length);
		for (int i=0; i < TEST_BYTES.length; i++) {
			assertEquals(bytesResult[i], TEST_BYTES[i]);
		}
	}
}
