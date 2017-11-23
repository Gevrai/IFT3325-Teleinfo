package receiver;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import frames.ConnectionFrame;
import frames.Frame;
import frames.MalformedFrameException;
import network.NetworkAbstraction;

public class ReceiverWorker extends Thread {
	
	protected NetworkAbstraction network;
	
	public ReceiverWorker(Socket clientSocket) throws IOException {
		this.network = new NetworkAbstraction(clientSocket);
	}
	
	@Override
	public void run() {
		try {
			if (!acceptConnection()) {
				// Do we simply drop the connection ?
				network.close();
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// TODO bulk of the client side connection handling should be done here
	private boolean acceptConnection() throws IOException {
		// This frame should be a connection frame
		Frame connectionFrame = network.receiveFrame();
		if (!(connectionFrame instanceof ConnectionFrame)) {
			return false;
		}
		
		return false;

	}
        
}
