package receiver;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Receiver {
	
	private ServerSocket serverSocket;

	public Receiver(int portNumber) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
	}
	
	// Runs a server instance, instantiating a thread upon receiving a new connection
	public void run() throws IOException {
		while (true) {
			Socket clientSocket = serverSocket.accept();
			ReceiverWorker receiverThread = new ReceiverWorker(clientSocket);
			receiverThread.start();
		}
	}

	public static void main(String[] args) {
		// Receiver <Numero_Port>
		if (args.length != 1) {
			System.out.println("Invalid arguments, should be of form : Receiver <Port_Number>");
			return;
		}

		// Creates and run a Receiver instance, verifying args syntax
		try { 
			int portNumber = Integer.parseUnsignedInt(args[0]);
			Receiver receiver = new Receiver(portNumber);
			receiver.run();
		} catch (NumberFormatException e) {
			System.out.println("Invalid port, should be a number : " + args[0] + "\n" + e.getMessage());
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
}
