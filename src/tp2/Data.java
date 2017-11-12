package tp2;

/**
 *
 * @author Sebastien
 */
public class Data {
    boolean notNull = false; // Is the data field non-null?
    byte b;
    short s;
    int i;
    long l;
    
    public Data(String sLine, int iNbCharactersInDataField){
        if(iNbCharactersInDataField != 0){
            notNull = true;
            if(iNbCharactersInDataField <= 8){
                this.b = Byte.valueOf(sLine);
            }
            else if(iNbCharactersInDataField <= 16){
                this.s = Short.valueOf(sLine);
            }
            else if(iNbCharactersInDataField <= 32){
                this.i = Integer.valueOf(sLine);
            }
            else{
                this.l = Long.valueOf(sLine);
            }
        }
    }
}
