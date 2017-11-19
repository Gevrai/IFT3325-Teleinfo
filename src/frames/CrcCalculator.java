package frames;

import java.math.BigInteger;

/**
 *
 * @author Sebastien
 */
public class CrcCalculator {
    public static byte[] calculateCRC(byte type, byte num, byte[] dataField) { 
        // Steps for calculation:
        
        // 1. Get ASCII values of every field.
        String asciiValueType = ConversionUtils.getASCIIValueAsString(type);
        String asciiValueNum = ConversionUtils.getASCIIValueAsString(num);
        String asciiValueDataField = ConversionUtils.getASCIIValueAsString(dataField);
        
        // 2. Concatenate the above ASCII values into one String:
        String asciiValues = asciiValueType + asciiValueNum + asciiValueDataField;
        
        // 3. Convert the above string of ASCII values into a BigInteger:
        BigInteger bAsciiValues = new BigInteger(asciiValues);
        
        // 4. Convert the above BigInteger into a String representation of its
        // binary value:
        String sBinary = bAsciiValues.toString(2);
        
        // 5. Convert the binary String into a BigInteger:
        BigInteger bBinary = new BigInteger(sBinary);
        
        // ***TODO:
        // 6. Get remainder of division of bBinary by the polynomial generator
        // G_X; that rest is the CRC.
        
        
        // 7. Convert bCRC to byte array; return the array.
                byte[] a = {1,2};
                return a;
        //return bCRC.toByteArray();
    }
}
