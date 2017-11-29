package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.FinalFrame;
import frames.Frame;
import frames.RejFrame;
import network.IFrameReceiver;
import network.NetworkAbstraction;
import network.ReceiveFrameBackgroundTask;
import utils.Log;

/** Abstract session for connection and disconnection with a receiver.
 *  This is the heart of the sender side of the program.
 *  
 *  Sub-classes need to implement the send() method, based on their algorithm.
 */
public abstract class Session implements IFrameReceiver {
	
	public static final int MAX_CONNECTION_ATTEMPTS = 1000;
    public static int TIMEOUT_TIME = 3000; // milliseconds
    
	protected NetworkAbstraction network;

	private ReceiveFrameBackgroundTask receiveFrameBackgroundTask = new ReceiveFrameBackgroundTask(this.network, this);
	protected Queue<Frame> receptionQueue = new LinkedList<Frame>();
	protected IOException receptionException = null;
	
	public Session(String machineName, int portNumber) throws UnknownHostException, IOException {
		this.network = new NetworkAbstraction(new Socket(machineName, portNumber));
		// Start the receiver background task for this network, notifying this session upon a frame reception
		this.receiveFrameBackgroundTask = new ReceiveFrameBackgroundTask(this.network, this);
		this.receiveFrameBackgroundTask.start();
	}
	
	public static void setTimeout(int timeout) { TIMEOUT_TIME = timeout; }

	public static Session connect(String machineName, int portNumber, byte connectionType) {
		Session session;
		try {
			// Creates a session and verifies if it's a valid type
			byte type = connectionType;
			switch (type) {
			case (ConnectionFrame.STOP_AND_WAIT) :
				session = new StopAndWaitSession(machineName, portNumber); break;
			case (ConnectionFrame.SELECTIVE_REJECT) :
				session =  new SelectiveRejectSession(machineName, portNumber); break;
			case (ConnectionFrame.GO_BACK_N) :
				session = new GoBackNSession(machineName, portNumber); break;
			default :
				Log.println("Connection type " + connectionType + " is unknown.");
				return null;
			}
			// Attempt connection
			if (session.attemptConnection(type, 0))
				return session;

		} catch (UnknownHostException e) {
			Log.println(e.getMessage());
		} catch (IOException e) {
			Log.println("IOException when attempting to create socket on hostname:" + machineName +" port:" + portNumber);
		}
		return null;
	}
	
	// Attempts a connection with the other end of network, this is stop-and-wait
	private boolean attemptConnection(byte connectionType, int nbConnectionAttempts) throws IOException {
		
		if (nbConnectionAttempts >= MAX_CONNECTION_ATTEMPTS) {
			Log.println("Could not connect to server : Max connection attempts (" + MAX_CONNECTION_ATTEMPTS + ") reached");
			return false;
		}
		
		Log.verbose("Sending Connection frame to '" + network.getHostName() + "'...");
		ConnectionFrame cframe = new ConnectionFrame(connectionType);
		network.sendFrame(cframe);
		// Event loop : wait for response frame for a maximum of TIMEOUT_TIME
		long currentTime = System.currentTimeMillis();
		while (currentTime + TIMEOUT_TIME > System.currentTimeMillis()) {
			// Throw exception from thread if there was one
			if (this.receptionException != null) 
				throw receptionException;
			// If we did not receive anything, repoll after small delay, else process received frame
			if (receptionQueue.isEmpty()) {
				try { Thread.sleep(2); } catch (Exception e) {/*Main thread cannot be killed */}
			} else {
				Frame receivedFrame = receptionQueue.poll();
				// Connection accepted
				if (receivedFrame.getType() == AckFrame.TYPE) {
					Log.verbose("Connection accepted");
					return true;
				}
				// Connection refused
				if (receivedFrame.getType() == RejFrame.TYPE) {
					Log.verbose("Connection refused");
					return false;
				}
				// Else we received something unusual, retry sending the frame?
				Log.verbose("Received invalid response frame, resending...");
				return attemptConnection(connectionType, nbConnectionAttempts+1);
			}
		}
		// Did not receive anything back, retry
		Log.verbose("Waiting for response timeout...");
		return attemptConnection(connectionType, nbConnectionAttempts+1);
	}
        
	public abstract boolean send(InputStream istream) throws IOException;
	
	public void notifyFrameReceived(Frame frame) {
		this.receptionQueue.add(frame);
	}
	
	public void notifyIOException(IOException e) {
		// Tell main thread there was an IOException on this network
		this.receptionException = e;
	}

	// Final frame is used to tell the server we are done
	public void close() throws IOException {
		Log.verbose("Closing connection with host::" + network.getHostName() + "...");
		network.sendFrame(new FinalFrame());
		// Drop everything in reception queue
		while (receptionQueue.poll() != null);
		// Close the network
		network.close();
	}
}
