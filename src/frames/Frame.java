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
    
    public static final byte flag = (byte) 0b01111110;
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
    
    // Reconstruct a frame from a received byte array
    public Frame(byte[] bFrame) throws MalformedFrameException {
    	if (bFrame[0] != Frame.flag || bFrame[bFrame.length-1] != Frame.flag) {
    		throw new MalformedFrameException();
    	}

    	int offset = 0;
    	byte[] bFrameClean = bitUnstuff(bFrame);
    	int dataSize = bFrameClean.length - (TYPE_FIELD_SIZE + NUM_FIELD_SIZE + CRC_FIELD_SIZE + 2*FLAG_FIELD_SIZE);

    	this.type = bFrameClean[offset];
    	offset += TYPE_FIELD_SIZE;

    	this.num = bFrameClean[offset];
    	offset += NUM_FIELD_SIZE;
    	
    	this.dataField = new byte[dataSize];
    	System.arraycopy(bFrameClean, offset, this.dataField, 0, dataSize);
    	offset += this.dataField.length;
    	
    	this.CRC = new byte[CRC_FIELD_SIZE];
    	System.arraycopy(bFrameClean, offset, this.CRC, 0, CRC_FIELD_SIZE);
    	
    }
    
    // TODO TODO TODO
    public byte[] bitStuff(byte[] bs) {
    	return bs;
    }

    // TODO TODO TODO
    public byte[] bitUnstuff(byte[] bs) {
    	return bs;
    }
    
    // Returns the byte representation of this frame, ready to be sent
    public byte[] getBytesFlagged() {
    	byte[] temp = bitStuff(getUnflaggedFrameAsBytes());
    	byte[] flaggedBytes = new byte[temp.length + 2*FLAG_FIELD_SIZE];
    	flaggedBytes[0] = Frame.flag;
    	flaggedBytes[flaggedBytes.length-1] = Frame.flag;
    	System.arraycopy(temp, 0, flaggedBytes, FLAG_FIELD_SIZE, temp.length);
    	return flaggedBytes;
    }
    
    // Returns this frame's internal information (no flags or bit stuffing) as as single byte array
    private byte[] getUnflaggedFrameAsBytes() {
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
}
