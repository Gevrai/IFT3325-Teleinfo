package utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class BinaryDivision {
	
	// Implements a CRC circuit in software as shown at p.194 of 'Data and Computer Communications 8th ed.'
    public static byte[] getRemainder(byte[] dividend, byte[] divisor) {
    	
    	// Imperative that divisor be of form [1][x][y]...[z], as in first byte equals exactly one.
    	if (divisor[0] != 1) 
    		return null;
    	Registers xorGates = new Registers(Arrays.copyOfRange(divisor, 1, divisor.length));
    	
    	// Input at each step is each bit of dividend and registers are initially all zeros
    	BitInputStream inputStream = new BitInputStream(new ByteArrayInputStream(dividend));
    	Registers reg = new Registers(Byte.SIZE*(divisor.length-1));
    	
		try {
			for(int i=1 ; i<(dividend.length*Byte.SIZE) ; i++) {
				int input = inputStream.readBit();
				int leftBit = reg.get(0);
				if ((input ^ leftBit) == 1)
					reg.XOR(xorGates.getBytes());
				reg.shiftleft();
				reg.set(reg.length()-1, (input ^ leftBit));
			}
		} catch (IOException e) { assert false; }
    	
    	return reg.getBytes();
    }

}