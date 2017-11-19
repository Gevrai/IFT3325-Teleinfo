package utils;

public class Registers {

	private byte[] registers;

	public Registers(int length) { 
		this.registers = new byte[length/8];
	}
	
	public Registers(byte[] bytes) {
		this.registers = bytes;
	}

	// Simply shifts everything once to the left, not caring about overflows
	public void shiftleft() { 
		// Shift one bit to left, and get next byte's MSD as LSD
		for (int i=0 ; i<registers.length-1 ; i++) {
			registers[i] <<= 1; 
			registers[i] |= registers[i+1] >>> 7;
		}
		// Last one has no next byte
		registers[registers.length-1] <<= 1;
	}
	
	// Applies the result of XOR with b to this register
	public void XOR(byte[] b) {
		assert b.length == registers.length;
		for (int i=0;i<b.length;i++) 
			this.registers[i] ^= b[i];
	}
	
	// Return number of registers
	public int length() { 
		return registers.length * 8; 
	}

	
	// Get the value of the bit at register i
	public int get(int i) { 
		return (registers[i/8] & (0b10000000 >>> (i%8))) == 0 ? 0 : 1; 
	}
	
	// Set the value of the bit at register i
	public void set(int i, int binValue) { 
		if (binValue != 0) {
			assert binValue == 1;
			registers[i/8] |= (0b10000000 >>> (i%8)); 
		}
	}

	// Compare two registers for content equality
	public boolean equals(Registers that) {
		if (this.length() != that.length())
			return false;
		for (int i=0 ; i<this.length() ; i++)
			if (this.get(i) != that.get(i))
				return false;
		return true;
	}
	
	public byte[] getBytes() { 
		return registers; }
	
	public void print() {
		String s = "";
		for (int i=0; i<registers.length*8 ; i++) {
			s += get(i) == 0 ? '0' : '1' ;
		}
		System.out.println(s);
	}
}
