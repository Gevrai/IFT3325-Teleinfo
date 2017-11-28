package receiver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	private ReceiverWorker receiverWorker;
	private OutputStream ostream;

	public Receiver(int portNumber, double errorRatio, OutputStream ostream) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
		this.errorRatio = errorRatio;
		this.ostream = ostream;
	}

	public Receiver(int portNumber) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
		this.errorRatio = 0.0;
	}
	
	public int getLocalPort() { return serverSocket.getLocalPort(); }
	
	public ReceiverWorker getReceiverWorker() { return this.receiverWorker; }
	
	public void acceptConnectionAndProcess() {
		while (true) {
			try {
				Log.verbose("\nAwaiting new connection");
				Socket clientSocket = serverSocket.accept();
				Log.verbose("New client attempting connection");
				this.receiverWorker = new ReceiverWorker(clientSocket, errorRatio);
				this.receiverWorker.setOuputStream(this.ostream);
				receiverWorker.startProcessing();
			} catch (IOException e) {/* Client disconneted... */ }
		}
	}

	public static void main(String[] args) {
		// Receiver <Numero_Port>
		if (args.length == 0 || args.length > 2) {
			System.out.println("Invalid arguments, should be of form : Receiver <portNumber> [<errorRatio>]");
			return;
		}

		// Creates and run a Receiver instance, verifying args syntax
		// Prints the reception to a file
		try { 

			int portNumber = Integer.parseUnsignedInt(args[0]);
			double errorRatio = args.length > 1 ? Double.parseDouble(args[1]) : 0.0;
			String outfileName = args.length > 2 ? args[2] : "defaultouput.txt";


			File outfile = new File(outfileName);
			OutputStream ostream = new FileOutputStream(outfile);
			if (!outfile.exists())
				outfile.createNewFile();

			Receiver receiver = new Receiver(portNumber, errorRatio, ostream);

			receiver.acceptConnectionAndProcess();

		} catch (NumberFormatException e) {
			Log.println("Invalid port, should be a number : " + args[0] + "\n" + e.getMessage());
			return;
		} catch (IOException e) {
			Log.println(e.getMessage());
			return;
		}
	}
}
