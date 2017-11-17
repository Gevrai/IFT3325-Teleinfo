package sender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import frames.ConnectionFrame;
import frames.Frame;

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
	public void sendFile(File file) throws IOException {
		Socket socket = new Socket(this.machineName, this.portNumber);
		OutputStream ostream = socket.getOutputStream();
		InputStream istream = socket.getInputStream();
		// Demande de connexion
		Frame f = new ConnectionFrame(ConnectionFrame.GO_BACK_N);
		ostream.write(f.getBytes());
		
		// Reception de l'accuse de reception

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
			File fileToSend = new File(args[2]);
			sender.sendFile(fileToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
