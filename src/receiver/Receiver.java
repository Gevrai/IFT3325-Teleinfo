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
		this.ostream = System.out;
	}
	
	public int getLocalPort() { return serverSocket.getLocalPort(); }
	
	public ReceiverWorker getReceiverWorker() { return this.receiverWorker; }
	
	public void acceptConnectionAndProcess() {
		try {
			Log.verbose("\nAwaiting new connection...");
			Socket clientSocket = serverSocket.accept();
			Log.verbose("New client connection");
			this.receiverWorker = new ReceiverWorker(clientSocket, errorRatio);
			this.receiverWorker.setOuputStream(this.ostream);
			receiverWorker.startProcessing();
		} catch (IOException e) { /* Client disconnected... */ }

		Log.verbose("Client disconnected...");
	}

	public static void main(String[] args) {
		// Receiver <Numero_Port>
		if (args.length == 0) {
			System.out.println("Invalid arguments, should be of form : Receiver <portNumber> [<errorRatio>] [<outputFileName>]");
			return;
		}

		// Creates and run a Receiver instance, verifying args syntax
		// Prints the reception to a file
		int portNumber = 0;
		double errorRatio = 0.0;

		// Assign port number
		try { portNumber = Integer.parseUnsignedInt(args[0]); }
		catch (NumberFormatException e) {
			Log.println("Invalid port " + args[0] + ", should be an integer\n" + e.getMessage());
			return;
		}

		// Set error ratio (defaults to 0.0)
		try { errorRatio = args.length > 1 ? Double.parseDouble(args[1]) : 0.0;
		if ((errorRatio < 0.0) || (errorRatio >= 1.0)) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			Log.println("Invalid error ratio " + args[1] + ", should be 0 <= errorRatio < 1\n");
			Log.println("Defaulting value to 0.0 (perfect communication");
		}

		// Output file name (defaults to 'output')
		OutputStream ostream;
		String outfileName = args.length > 2 ? args[2] : "ouput";
		try {
			File outfile = new File(outfileName);
			outfile.delete();
			outfile.createNewFile();
			ostream = new FileOutputStream(outfile, false);
		} catch (IOException e) {
			Log.println("Could not overwrite file '" + outfileName + "'... Aborting");
			return;
		}

		// Start receiver
		try {
			Receiver receiver = new Receiver(portNumber, errorRatio, ostream);
			receiver.acceptConnectionAndProcess();
		} catch (IOException e) {
			Log.println("Could not create ServerSocket on port " + portNumber + "... Aborting");
		}
		
		System.exit(0);
	}
}
