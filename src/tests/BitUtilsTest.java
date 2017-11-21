package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import network.CRCCalculator;
import utils.BitInputStream;
import utils.BitOutputStream;

public class BitUtilsTest {

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
	
	@Test
	public void CRCCalulatorTest() {
		
		// Values taken from Exercice 7.2 of first homework
		byte[] dividend = new byte[]{ (byte) 0b10000000, 0b00000000};
		byte[] divisor = new byte[]{0b1, 0b00010000, 0b00100001};
		byte[] remainder = {0b00011011, (byte) 0b10011000};

		// Verify crc calculation yields the good crc code from precalculated result
		CRCCalculator crcCalculator = new CRCCalculator(divisor);
		byte[] result = crcCalculator.getCRC(dividend);
		assertTrue(Arrays.equals(remainder, result));
		
		// Check if doing the same calculation with the crc appended yields 0
		byte[] withCRC = Arrays.copyOf(dividend, dividend.length + result.length);
		System.arraycopy(result, 0, withCRC, dividend.length, result.length);
		
		result = crcCalculator.getCRC(withCRC);
		for (byte b : result)
			assertEquals(0,b);
	}
}
