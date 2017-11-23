package sessions;

import frames.SenderTimer;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.Frame;
import java.util.Timer;
import network.NetworkAbstraction;

public abstract class Session {
	
    private final int TIMER_UPPER_BOUND = 3000; // milliseconds
    
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

	private boolean attemptConnection(byte connectionType) throws IOException {
            
            ConnectionFrame cFrame = new ConnectionFrame(connectionType);
            
            Timer timer = new Timer();
            timer.schedule(new SenderTimer(connectionType, cFrame, network), 0, TIMER_UPPER_BOUND);
            
            Frame frame = network.receiveFrame();
            if (frame instanceof AckFrame)
                    return true;
            return false;
	}

	public abstract boolean send(InputStream istream) throws IOException ;

	// Since the protocol doesn't have a proper Disconnect Frame, we abruptly disconnect
	public boolean close() throws IOException {
            socket.close();
            return true;
	}
        
}
