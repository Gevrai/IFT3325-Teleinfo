package frames;

import utils.BinaryDivision;

public abstract class Frame {
    
    // Binary series corresponding to x^16 + x^12 + x^5 + 1: 10001000000100001
    
    // Polynôme générateur utilisé dans ce travail.
    public static final byte[] GX16 = new byte[] {0b1, 0b00010000, 0b00100001};
    
    // Taille maximale d'une frame pour la transmission, en bytes.
    public static final int MAX_FRAME_SIZE = 1500;
    
    // Tailles des différents champs en nombre de bytes.
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
    
    protected Frame(byte type, byte num, byte[] data){
        this.type = type;
        this.num = num;
        this.dataField = data;
    }
    
    protected Frame(byte type, byte num) {
    	this(type, num, new byte[0]);
    }
    
    // Returns this frame's internal information (no flags or bit stuffing) as as single byte array
    public byte[] getBytesWithCRC() {
    	int offset = 0;
    	byte[] bs = new byte[TYPE_FIELD_SIZE + NUM_FIELD_SIZE + this.dataField.length + CRC_FIELD_SIZE];

    	bs[offset] = this.type;
    	offset += TYPE_FIELD_SIZE;

    	bs[offset] = this.num;
    	offset += NUM_FIELD_SIZE;

    	System.arraycopy(this.dataField, 0, bs, offset, this.dataField.length);
    	offset += this.dataField.length;

    	// CRC calculations only done when sending
    	for (int i=0; i<CRC_FIELD_SIZE; i++) 
    		bs[i+offset] = 0;
    	byte[] crc = BinaryDivision.getRemainder(bs,GX16);
    	System.arraycopy(crc, 0, bs, offset, CRC_FIELD_SIZE);
    	return bs;
    }
    
    // Getters
    public byte getNum(){
        return this.num;
    }
}
