package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastien
 */
public class FileBufferedReader {
    
    public FileBufferedReader(){
        
    }
    
    public ArrayList<String> readFile(File file){
        if(file.exists()){
            try {
                Scanner scn;
                ArrayList<String> sResultsOfReading = new ArrayList();
                String sLigne;
                scn = new Scanner(file);
                
                while(scn.hasNextLine()){
                    sLigne = scn.nextLine();
                    sResultsOfReading.add(sLigne);
                    // Il faudra parcourir le tableau 'stringResultsOfReading'
                    // pour assembler les trames un élément à la fois, et
                    // rajouter les flags au début et à la fin de chaque trame
                    // au fur et à mesure.
                }
                return sResultsOfReading;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileBufferedReader.
                        class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
