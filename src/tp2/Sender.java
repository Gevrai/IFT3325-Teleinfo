package tp2;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Sebastien
 */
public class Sender {
    // Tailles des différents champs en nombre de caractères:
    private final int FLAG_FIELD_SIZE = 8;
    private final int TYPE_FIELD_SIZE = 1;
    private final int NUM_FIELD_SIZE = 8;
    private final int CRC_FIELD_SIZE = 16;
    
    // private Frame frame;
    
    public Sender(){}
    
    private void assembleLine(File file){
        
        FileBufferedReader fbr = new FileBufferedReader();
        ArrayList<String> fileInfo = fbr.readFile(file);
        ArrayList<String[]> sFrames = new ArrayList();
        
        // La ligne contenant les 6 éléments d"une trame:
        String[] presentLine;
        
        int presentIndex = 0;
        int iNbOnes = 0;
        int iNbCharactersInDataField;
        
        // Il faut construire les lignes une à une selon ce qui a été lu dans le
        // fichier et stocké dans 'fileInfo'; chaque ligne doit correspondre à
        // une trame.
        for(int i=0; i<fileInfo.size(); i++){
            iNbCharactersInDataField = 0;
            
            presentLine = new String[7];
            
            
            // Flag de début de trame:
            presentLine[0] += fileInfo.get(i).
                    substring(presentIndex, FLAG_FIELD_SIZE);
            presentIndex += FLAG_FIELD_SIZE;
            
            
            // Type:
            presentLine[1] += fileInfo.get(i).
                    substring(presentIndex, TYPE_FIELD_SIZE);
            presentIndex += TYPE_FIELD_SIZE;
            
            
            // Num:
            while((presentIndex + NUM_FIELD_SIZE + CRC_FIELD_SIZE
                    + FLAG_FIELD_SIZE) < fileInfo.get(i).length()){
                if((fileInfo.get(i).substring(presentIndex, presentIndex+1)).
                        equals("1")){
                    iNbOnes++;
                    
                    if(iNbOnes == 5){
                        iNbOnes = 0;
                        presentLine[2] += "0"; // Bit stuffing.
                    }
                }
                else{
                    iNbOnes = 0;
                }
                presentLine[2] += fileInfo.get(i).
                        substring(presentIndex, presentIndex+1);
                presentIndex++;
            }
            
            
            // Données:
            while((presentIndex + CRC_FIELD_SIZE + FLAG_FIELD_SIZE) 
                    < fileInfo.get(i).length()){
                if((fileInfo.get(i).substring(presentIndex, presentIndex+1)).
                        equals("1")){
                    iNbOnes++;
                    
                    if(iNbOnes == 5){
                        iNbOnes = 0;
                        presentLine[3] += "0"; // Bit stuffing.
                        iNbCharactersInDataField++;
                    }
                }
                else{
                    iNbOnes = 0;
                }
                presentLine[3] += fileInfo.get(i).
                        substring(presentIndex, presentIndex+1);
                presentIndex++;
                iNbCharactersInDataField++;
            }
            
            
            // CRC:
            while((presentIndex + FLAG_FIELD_SIZE) < fileInfo.get(i).length()){
                if((fileInfo.get(i).substring(presentIndex, presentIndex+1)).
                        equals("1")){
                    iNbOnes++;
                    
                    if(iNbOnes == 5){
                        iNbOnes = 0;
                        presentLine[4] += "0"; // Bit stuffing.
                    }
                }
                else{
                    iNbOnes = 0;
                }
                presentLine[4] += fileInfo.get(i).
                        substring(presentIndex, presentIndex+1);
                presentIndex++;
            }
            
            
            // Flag de fin de trame:
            presentLine[5] += fileInfo.get(i).
                    substring(presentIndex, FLAG_FIELD_SIZE);
            
            
            // Nombre de caractères dans le champ de données (nécessaire pour
            // savoir quelle taille/type de variable utiliser pour le champ de
            // données au moment de bâtir la trame):
            presentLine[6] = String.valueOf(iNbCharactersInDataField);
            
            
            // Stocker, dans 'sFrames', une ligne qui deviendra une trame.
            sFrames.add(presentLine.clone());
        }
    }
}
