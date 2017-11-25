package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.Frame;
import network.NetworkAbstraction;
import utils.FrameReceptionTimeoutCallable;
import utils.FrameReceptionTimeoutException;

public abstract class Session {
	
	protected ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    protected final int TIMEOUT_TIME = 3000; // milliseconds
    
	private Socket socket;
	protected NetworkAbstraction network;
	
	public Session(String machineName, String portNumber) throws NumberFormatException, UnknownHostException, IOException {
		Socket socket = new Socket(machineName, Integer.parseUnsignedInt(portNumber));
		this.network = new NetworkAbstraction(socket);
	}

	public static Session connect(String machineName, String portNumber, String connectionType) {
		Session session = null;
		try {
			// Creates a session and verifies if it's a valid type
			int type = Integer.parseInt(connectionType);
			switch (type) {
			case (ConnectionFrame.STOP_AND_WAIT) :
				session = new StopAndWaitSession(machineName, portNumber);
				break;
			case (ConnectionFrame.SELECTIVE_REJECT) :
				session =  new SelectiveRejectSession(machineName, portNumber);
				break;
			case (ConnectionFrame.GO_BACK_N) :
				session = new GoBackNSession(machineName, portNumber);
				break;
			default :
				System.err.println("Connection type " + connectionType + " is unknown.");
				return null;
			}
			// Attempt connection
			if (session.attemptConnection(Byte.parseByte(connectionType)))
				return session;
			
		} catch (NumberFormatException e) {
			System.err.println("Connection type " + connectionType + " is invalid : Should be an integer.");
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException when attempting to create socket on hostname:" + machineName +" port:" + portNumber);
		}
		return null;
	}
	
	public ScheduledFuture<Void> setTimeout(Frame frame) {
		return scheduler.schedule(new FrameReceptionTimeoutCallable(frame), TIMEOUT_TIME, TimeUnit.MILLISECONDS);
	}

	private boolean attemptConnection(byte connectionType) throws IOException {
		
		int MAX_CONNECTION_ATTEMPTS = 10;
		boolean isConnectionEstablished = false;
		ConnectionFrame cframe = new ConnectionFrame(connectionType);

		for (int nbAttempts = 0 ; nbAttempts < MAX_CONNECTION_ATTEMPTS; nbAttempts++) {
			try {
				// Send connection frame and start a timer
				network.sendFrame(cframe);
				ScheduledFuture<Void> timeout = setTimeout(cframe);
				// Receive the frame and verify it
				Frame frame = network.receiveFrame();
				Timeout t = new Timeout
				timeout.get();
				switch (frame.getType()) {
				// Receive ACK frame, connection is OK
				case (AckFrame.TYPE) :
					timeout.cancel(true);
					return true;
				// Anything else, reattempt
				default : 
					timeout.cancel(true);
				}
			} catch (FrameReceptionTimeoutException e) {
				// Nothing to do, just resend resend
				System.err.println("Could not receive ACK for frame " + e.getFrame().getNum() + ": Resending");
			}
		}
	}
        
	public abstract boolean send(InputStream istream) throws IOException ;

	// Since the protocol doesn't have a proper Disconnect Frame, we abruptly disconnect
	public boolean close() throws IOException {
            socket.close();
            return true;
	}
        
}
