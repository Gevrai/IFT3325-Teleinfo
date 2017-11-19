package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import utils.BinaryDivision;
import utils.BitInputStream;
import utils.BitOutputStream;
import utils.Registers;

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
	public void RegistersTest() {
		
		Registers r1, r2;
		
		r1 = new Registers(16);
		r2 = new Registers(16);
		
		assertEquals(16, r1.length());
		
		r1.set(3, 1);
		r1.set(8, 1);
		r1.set(0, 1);
		
		assertEquals(1, r1.get(3));
		assertEquals(0, r1.get(1));

		//assertEquals(new byte[] {0b00010000, (byte) 0b10000000}, r1.getBytes() );

		r2.set(4, 1);
		r2.set(9, 1);
		r2.set(1, 1);
		r2.shiftleft();
		assertTrue(r1.equals(r2));
		r2.shiftleft();
		assertFalse(r1.equals(r2));
	}
	
	@Test
	public void BinaryDivisionTest() {
		
		// Exercice 7.2 from first homework
		byte[] dividend = new byte[]{ (byte) 0b10000000, 0b00000000, 0b00000000, 0b00000000 };
		byte[] divisor = new byte[]{0b1, 0b00010000, 0b00100001};
		byte[] remainder = {0b00011011, (byte) 0b10011000};
		byte[] result = BinaryDivision.getRemainder(dividend, divisor);
		//assertEquals(remainder, result);
		
	}
}
