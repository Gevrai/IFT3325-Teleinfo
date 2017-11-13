package tp2;

/**
 *
 * @author Sebastien
 */
public class Frame {
    private byte flagStart; // 1 byte
    private byte type; // 1 byte
    private byte num; // 1 byte
    private Data dataField; // size varies
    private short CRC; // 2 bytes
    private byte flagEnd; // 1 byte
    
    public Frame(String[] line, Data dataField){
        this.flagStart = Byte.valueOf(line[0]);
        this.type = Byte.valueOf(line[1]);
        this.num = Byte.valueOf(line[2]);
        this.dataField = dataField;
        this.CRC = Short.valueOf(line[4]);
        this.flagEnd = Byte.valueOf(line[5]);
    }
}
