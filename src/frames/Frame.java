package frames;

import java.math.BigInteger;
import utils.BinaryDivision;

public class Frame {
    
    // Binary series corresponding to x^16 + x^12 + x^5 + 1: 10001000000100001
    
    // Polynôme générateur utilisé dans ce travail.
    public static final BigInteger G_X = new BigInteger("10001000000100001");
    
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
    private byte[] CRC;
    
    public Frame(byte type, byte num, byte[] data){
        this.type = type;
        this.num = num;
        this.dataField = data;
        this.CRC = calculateCRC(type, num, dataField);
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
    
    public byte[] calculateCRC(byte type, byte num, byte[] dataField) { 
        // Steps for calculation:
        
        // 1. Get ASCII values of every field.
        String asciiValueType = getASCIIValueAsString(type);
        String asciiValueNum = getASCIIValueAsString(num);
        String asciiValueDataField = getASCIIValueAsString(dataField);
        
        // 2. Concatenate the above ASCII values into one String:
        String asciiValues = asciiValueType + asciiValueNum + asciiValueDataField;
        
        // 3. Convert the above string of ASCII values into a BigInteger:
        BigInteger bAsciiValues = new BigInteger(asciiValues);
        
        // 4. Convert the above BigInteger into a String representation of its
        // binary value:
        String sBinary = bAsciiValues.toString(2);
        
        // 5. Convert the binary String into a BigInteger:
        BigInteger bBinary = new BigInteger(sBinary);
        
        // 6. Get rest of division of bBinary by the polynomial generator G_X;
        // that rest is the CRC.
        BigInteger bCRC = bBinary.mod(G_X);
        
        // 7. Convert bCRC to byte array; return the array.
        return bCRC.toByteArray();
    }
    
    // TODO do the real calculation !
    public boolean isValidCRC(byte[] dataPlusCRC) {
        String sDataPlusCRC = String.valueOf(dataPlusCRC);
        BinaryDivision bd = new BinaryDivision();
        
        String sResult = bd.getRemainder(sDataPlusCRC, G_X.toString(2));
        
        // Check if the answer contains "1"; if yes, return false; if
        // not, return true.
        if(sResult.contains("1")){
            return false;
        }
    	return true;
    }
    
    private String getASCIIValueAsString(byte aByte){
        byte[] b = {aByte};
        
        // Convert byte to String:
        String sBytes = bytesToStringUTFCustom(b);
        
        // Get ASCII value of every character in the String and concatenate the
        // results:
        String asciiValues = "";
        int asciiValue;
        for(int i=0; i<sBytes.length(); i++){
            asciiValue = (int)(sBytes.charAt(i));
            asciiValues += String.valueOf(asciiValue);
        }
        
        return asciiValues;
    }
    
    private String getASCIIValueAsString(byte[] someBytes){
        // Convert byte to String:
        String sBytes = bytesToStringUTFCustom(someBytes);
        
        // Get ASCII value of every character in the String and concatenate the
        // results:
        String asciiValues = "";
        int asciiValue;
        for(int i=0; i<sBytes.length(); i++){
            asciiValue = (int)(sBytes.charAt(i));
            asciiValues += String.valueOf(asciiValue);
        }
        
        return asciiValues;
    }
    
    public static String bytesToStringUTFCustom(byte[] bytes){
        char[] buffer = new char[bytes.length >> 1];
        for(int i = 0; i < buffer.length; i++){
            int bpos = i << 1;
            char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));
            buffer[i] = c;
        }
        return new String(buffer);
    }
}
