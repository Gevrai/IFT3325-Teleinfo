package sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import sessions.Session;

public abstract class Sender {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>
		if (args.length != 4) {
			System.err.println("Invalid arguments, should be of form : Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
			return;
		}
                
		String machineName = args[0];
		String portNumber = args[1];
		String fileName = args[2];
		String connectionType = args[3];
                
		try {
			InputStream fileStream = new FileInputStream(new File(fileName));
			// Connect the session
			Session session = Session.connect(machineName, portNumber, connectionType);
			if (session == null) {
				System.err.println("Could not create Session... Exiting...");
				return;
			}
			// Send the file
			session.send(fileStream);
			
			// Close the connection
			session.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		} catch (IOException e) {
			return;
		}
	}

}
