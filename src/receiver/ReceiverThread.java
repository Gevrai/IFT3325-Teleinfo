package receiver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReceiverThread extends Thread {
	
	private static final int BUF_SIZE = 512;
	
	private final Socket clientSocket;
	private byte[] buf;
	
	public ReceiverThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.buf = new byte[BUF_SIZE];
	}
	
	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// TODO bulk of the client side connection handling should be done here
	private void handleClientSocket() throws IOException {
		int nbBytesRead, nbBytesWritten;
		OutputStream ostream = clientSocket.getOutputStream();
		InputStream istream = clientSocket.getInputStream();
		
		// Beginning of connection, MUST be a C Frame
		nbBytesRead = istream.read(buf);
		
		System.out.println("Sending to client on socket " + this.clientSocket);
		ostream.write(("TEST Writing to socket" + clientSocket + "\n").getBytes());
		clientSocket.close();
	}
}
