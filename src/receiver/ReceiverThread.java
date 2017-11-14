package receiver;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ReceiverThread extends Thread {

	private final Socket clientSocket;
	
	public ReceiverThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
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
		OutputStream os = clientSocket.getOutputStream();
		os.write(("TEST Writing to socket" + clientSocket + "\n").getBytes());
		clientSocket.close();
	}
}
