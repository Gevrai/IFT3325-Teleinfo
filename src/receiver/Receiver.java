package receiver;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.Log;

/** Main class for Receiver
 * 
 *  Usage : Receiver <Numero_Port>
 *
 */
public class Receiver {
	
	private ServerSocket serverSocket;
	private double errorRatio;

	public Receiver(int portNumber, double errorRatio) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
		this.errorRatio = errorRatio;
	}

	public Receiver(int portNumber) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
		this.errorRatio = 0.0;
	}
	
	public int getLocalPort() { return serverSocket.getLocalPort(); }
	
	// Instantiates a new thread upon receiving a new connection
	public void acceptConnections() throws IOException {
		while (true) {
			Socket clientSocket = serverSocket.accept();
			ReceiverWorker receiverThread = new ReceiverWorker(clientSocket, errorRatio);
			receiverThread.start();
		}
	}

	public static void main(String[] args) {
		// Receiver <Numero_Port>
		if (args.length == 0 || args.length > 2) {
			System.out.println("Invalid arguments, should be of form : Receiver <Port_Number> [<errorRatio>]");
			return;
		}

		// Creates and run a Receiver instance, verifying args syntax
		try { 
			int portNumber = Integer.parseUnsignedInt(args[0]);
			double errorRatio = args.length == 2 ? Double.parseDouble(args[1]) : 0.0;
			Receiver receiver = new Receiver(portNumber, errorRatio);
			receiver.acceptConnections();
		} catch (NumberFormatException e) {
			Log.println("Invalid port, should be a number : " + args[0] + "\n" + e.getMessage());
			return;
		} catch (IOException e) {
			Log.println(e.getMessage());
			return;
		}
	}
}
