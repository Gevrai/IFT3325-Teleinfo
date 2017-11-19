package frames;

/**
 *
 * @author Sebastien
 */
public class ConversionUtils {
    
    public static String getASCIIValueAsString(byte aByte){
        byte[] b = {aByte};
        
        // Convert byte to String:
        String sBytes = bytesToString(b);
        
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
    
    public static String getASCIIValueAsString(byte[] someBytes){
        // Convert byte to String:
        String sBytes = bytesToString(someBytes);
        
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
    
    public static String bytesToString(byte[] someBytes){
        char[] cBuffer = new char[someBytes.length >> 1];
        for(int i=0; i<cBuffer.length; i++){
            int iBytePos = i<<1;
            char c = (char)(((someBytes[iBytePos]&0x00FF)<<8) + (someBytes[iBytePos+1]&0x00FF));
            cBuffer[i] = c;
        }
        return new String(cBuffer);
    }
}
