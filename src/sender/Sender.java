package sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sender {

	String      machineName;
	int         portNumber;
	private String      fileName;
	private int         goBackAmount;
        
	public Sender(String machineName, String portNumber, String fileName, String goBackAmount) {
		// TODO vérifier la validité des informations données
		this.machineName = machineName;
		this.portNumber = Integer.parseUnsignedInt(portNumber);
		this.fileName = fileName;
		this.goBackAmount = Integer.parseUnsignedInt(goBackAmount);
	}
	
	// Just testing.....
	public void sendFile(File file) throws IOException {
		Socket socket = new Socket(this.machineName, this.portNumber);
		OutputStream ostream = socket.getOutputStream();
		InputStream istream = socket.getInputStream();
	}
        
        public void sendData(Socket soc) throws IOException {
            try{
                DataOutputStream dataOutput = new DataOutputStream(soc.getOutputStream());
                
                // TODO: send data...
            }
            catch(IOException e){
                System.out.println(e);
            }
        }
        
	public static void main(String[] args) {
		// Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>
		if (args.length != 4) {
			// ERROR !
			System.out.println("Invalid arguments, should be of form : Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
			return;
		}
                
	}
        
        // Getters
        public String getMachineName(){
            return this.machineName;
        }
        public int getPortNumber(){
            return this.portNumber;
        }
        public String getFileName(){
            return this.fileName;
        }
        public int getGoBackAmount(){
            return this.goBackAmount;
        }
}
