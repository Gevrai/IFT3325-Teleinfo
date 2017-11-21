package network;

import java.util.Arrays;

public class CRCCalculator {
	
	byte[] generator;
	
	public CRCCalculator(byte[] generator) {
    	// Imperative that divisor be of form [1][x][y]...[z], as in first byte equals exactly one.
		if (generator[0] != 1)
			System.out.println("INVALID CRC GENERATOR : " + Arrays.toString(generator));
		this.generator = Arrays.copyOfRange(generator, 1, generator.length);
	}
	
	private int getBit(int pos, byte[] data) {
		return (data[pos/8] & (0b10000000 >>> (pos%8))) == 0 ? 0 : 1;
	}
	
	// Bitwise shift left of a whole byte array once
	private void shiftLeft(byte[] bytes) {
		for (int i=0 ; i<bytes.length-1 ; i++) {
			bytes[i] <<= 1; 
			// 0xFF masking because of signed byte conversion to int
			bytes[i] = (byte) (bytes[i] ^ ((0xFF & bytes[i+1]) >>> 7));
		}
		// Last one has no next byte
		bytes[bytes.length-1] <<= 1;
	}
	
	// Implements a CRC circuit in software as shown at p.194 of 'Data and Computer Communications 8th ed.'
    public byte[] getCRC(byte[] data) {
    	
    	// Initial registers are all zeros
    	byte[] registers = new byte[this.generator.length];
    	
    	// Main loop, feeding one bit from data at each iteration
		for(int i=0 ; i<(data.length*Byte.SIZE) ; i++) {
			// Get input bit and registers MSB for current iteration
			int inputBit = getBit(i, data);
			int leftBit = getBit(0, registers);
			
			// Register shift left
			shiftLeft(registers);
			
			if ((inputBit ^ leftBit) == 1)
				// Generator affects registers, we do (registers ^ generator)
				for(int j=0; j<registers.length;j++)
					registers[j] ^= generator[j];
		}
    	
		// Content of register is the CRC (also the remainder of division)
    	return registers;
    }

}