package tp2;

/**
 *
 * @author Sebastien
 */
public class Frame {
    private final byte flagStart; // 1 byte
    private final byte type; // 1 byte
    private final byte num; // 1 byte
    private final Data dataField; // size varies
    private final short CRC; // 2 bytes
    private final byte flagEnd; // 1 byte
    
    public Frame(String[] line, Data dataField){
        this.flagStart = Byte.valueOf(line[0]);
        this.type = Byte.valueOf(line[1]);
        this.num = Byte.valueOf(line[2]);
        this.dataField = dataField;
        this.CRC = Short.valueOf(line[4]);
        this.flagEnd = Byte.valueOf(line[5]);
    }
}
