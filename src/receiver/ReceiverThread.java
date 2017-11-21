package receiver;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import frames.Frame;
import frames.MalformedFrameException;
import network.NetworkAbstraction;

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
            try {
                int nbBytesRead, nbBytesWritten;
                OutputStream ostream = clientSocket.getOutputStream();
                InputStream istream = clientSocket.getInputStream();
                // Beginning of connection, MUST be a C Frame
                nbBytesRead = istream.read(buf);
                
                Frame f = receiveData(new DataInputStream(istream));
                
                System.out.println("Sending to client on socket " + this.clientSocket);
                
                //ostream.write(("TEST Writing to socket" + clientSocket + "\n").getBytes());
                
                clientSocket.close();
            } catch (MalformedFrameException ex) {
                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
        
        private Frame receiveData(DataInputStream dataInput) throws IOException, MalformedFrameException {
            Frame frame = null;
            frame = NetworkAbstraction.receiveFrame(dataInput);
            return frame;
        }
}
