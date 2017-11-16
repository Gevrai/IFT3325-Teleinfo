package frames;

import java.io.File;
import java.util.ArrayList;

import utils.FileBufferedReader;

public class Frame {
	
	// Taille maximal d'une frame pour la transmission, en bytes
	public static final int MAX_FRAME_SIZE = 1500;

    // Tailles des différents champs en nombre de bytes
    private static final int FLAG_FIELD_SIZE = 1;
    private static final int TYPE_FIELD_SIZE = 1;
    private static final int NUM_FIELD_SIZE = 1;
    private static final int CRC_FIELD_SIZE = 2;

    // | 1 Byte | 1 Byte | 1 Byte |    X Byte(s)   | 2 Bytes | 1 Byte |
    // |--------|--------|--------|----------------|---------|--------|
    // |  Flag  |  Type  |  Num   |      Data      |   CRC   |  Flag  |
    // |--------|--------|--------|----------------|---------|--------|
    
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
    
    // Reconstruct a frame from a received byte array
    public Frame(byte[] bFrame) throws MalformedFrameException {
    	int offset = 0;
    	int dataSize = bFrame.length - (TYPE_FIELD_SIZE + NUM_FIELD_SIZE + CRC_FIELD_SIZE + 2*FLAG_FIELD_SIZE);

    	this.type = bFrame[offset];
    	offset += TYPE_FIELD_SIZE;

    	this.num = bFrame[offset];
    	offset += NUM_FIELD_SIZE;
    	
    	this.dataField = new byte[dataSize];
    	System.arraycopy(bFrame, offset, this.dataField, 0, dataSize);
    	offset += this.dataField.length;
    	
    	this.CRC = new byte[CRC_FIELD_SIZE];
    	System.arraycopy(bFrame, offset, this.CRC, 0, CRC_FIELD_SIZE);
    }
    
    // Returns this frame's internal information (no flags or bit stuffing) as as single byte array
    public byte[] getBytes() {
    	byte[] bs = new byte[TYPE_FIELD_SIZE + NUM_FIELD_SIZE + this.dataField.length + CRC_FIELD_SIZE];
    	int offset = 0;
    	bs[offset] = this.type; 
    	offset += TYPE_FIELD_SIZE;
    	bs[offset] = this.num; 
    	offset += NUM_FIELD_SIZE;
    	System.arraycopy(this.dataField, 0, bs, offset, this.dataField.length);
    	offset += this.dataField.length;
    	// FIXME Assumes little-endian, which I think is fine ??
    	System.arraycopy(this.CRC, 0, bs, offset, CRC_FIELD_SIZE);
    	return bs;
    }
    
    // TODO do the real calculation ! :P
    public byte[] calculateCRC() {
    	return new byte[2];
    }
    
}
