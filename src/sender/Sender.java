package sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Sender {

	private String 	machineName;
	private int 	portNumber;
	private String 	fileName;
	private int 	goBackAmmount;
    
	public Sender(String machineName, String portNumber, String fileName, String goBackAmmount) {
		// TODO vérifier la validité des informations données
		this.machineName = machineName;
		this.portNumber = Integer.parseUnsignedInt(portNumber);
		this.fileName = fileName;
		this.goBackAmmount = Integer.parseUnsignedInt(goBackAmmount);
	}
	
	// Just testing.....
	public void run() throws IOException {
		byte buffer[] = new byte[256];
		Socket socket = new Socket(this.machineName, this.portNumber);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String line;
		while ( (line = reader.readLine()) != null) {
			System.out.println(line);
		}

	}

	public static void main(String[] args) {
		// Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>
		if (args.length != 4) {
			// ERROR !
			System.out.println("Invalid arguments, should be of form : Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
			return;
		}
		
		try {
			Sender sender = new Sender(args[0], args[1], args[2], args[3]);
			sender.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
