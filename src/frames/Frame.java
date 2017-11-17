package frames;

import java.io.File;
import java.util.ArrayList;

import utils.FileBufferedReader;

public class Frame {
	
	// Taille maximal d'une frame pour la transmission, en bytes
	public static final int MAX_FRAME_SIZE = 1500;

    // Tailles des différents champs en nombre de bytes
    public static final int FLAG_FIELD_SIZE = 1;
    public static final int TYPE_FIELD_SIZE = 1;
    public static final int NUM_FIELD_SIZE = 1;
    public static final int CRC_FIELD_SIZE = 2;

    // | 1 Byte | 1 Byte |    X Byte(s)   | 2 Bytes |
    // |--------|--------|----------------|---------|
    // |  Type  |  Num   |      Data      |   CRC   |
    // |--------|--------|----------------|---------|
    
    private byte type;
    private byte num;
    private byte[] dataField;
    private byte[] CRC;
    
    public Frame(byte type, byte num, byte[] data){
        this.type = type;
        this.num = num;
        this.dataField = data;
        this.CRC = calculateCRC();
    }
    
    public Frame(byte type, byte num) {
    	this(type, num, new byte[0]);
    }
    
    // Returns this frame's internal information (no flags or bit stuffing) as as single byte array
    public byte[] getBytes() {
    	int offset = 0;
    	byte[] bs = new byte[TYPE_FIELD_SIZE + NUM_FIELD_SIZE + this.dataField.length + CRC_FIELD_SIZE];

    	bs[offset] = this.type; 
    	offset += TYPE_FIELD_SIZE;

    	bs[offset] = this.num; 
    	offset += NUM_FIELD_SIZE;

    	System.arraycopy(this.dataField, 0, bs, offset, this.dataField.length);
    	offset += this.dataField.length;

    	System.arraycopy(this.CRC, 0, bs, offset, CRC_FIELD_SIZE);
    	return bs;
    }
    
    // TODO do the real calculation !
    public byte[] calculateCRC() {
    	byte[] temp = {0,0};
    	return temp;
    }
    
    // TODO do the real calculation !
    public boolean isValidCRC() {
    	return true;
    }
    
}
