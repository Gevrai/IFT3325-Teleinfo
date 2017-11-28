package frames;

/** Base frame doing all the legwork needed of a general frame.
 *  The data field might be empty.
 * 
 *   | 1 Byte | 1 Byte |    X Byte(s)   |
 *   |--------|--------|----------------|
 *   |  Type  |  Num   |      Data      |
 *   |--------|--------|----------------|
 *
 */
public abstract class Frame {
    
    // Taille maximale d'une frame pour la transmission, en bytes.
    public static final int MAX_FRAME_SIZE = 23;
    
    // Le champ num ne peux prendre que les valeurs entre 0 et 7;
    public static final int MAX_NUM = 8;

    // Tailles des différents champs en nombre de bytes.
    public static final int FLAG_FIELD_SIZE = 1;
    public static final int TYPE_FIELD_SIZE = 1;
    public static final int NUM_FIELD_SIZE = 1;
    public static final int CRC_FIELD_SIZE = 2;
    public static final int MAX_DATA_SIZE = MAX_FRAME_SIZE - (TYPE_FIELD_SIZE + NUM_FIELD_SIZE + CRC_FIELD_SIZE);
    
    private byte type;
    private byte num;
    private byte[] data;
    
    protected Frame(byte type, byte num, byte[] data){
        this.type = type;
        this.num = num;
        this.data = data;
    }
    
    protected Frame(byte type, byte num) {
    	this(type, num, new byte[0]);
    }

    public byte getType(){ return this.type; }
    public byte getNum(){ return this.num; }
    public byte[] getData(){ return this.data; }
    
    // Returns this frame's internal information (no flags or bit stuffing) as as single byte array
    public byte[] getBytes() {
    	int offset = 0;
    	byte[] bs = new byte[TYPE_FIELD_SIZE + NUM_FIELD_SIZE + this.data.length];

    	bs[offset] = this.type;
    	offset += TYPE_FIELD_SIZE;

    	bs[offset] = this.num;
    	offset += NUM_FIELD_SIZE;

    	System.arraycopy(this.data, 0, bs, offset, this.data.length);
    	offset += this.data.length;

    	return bs;
    }
    
}
