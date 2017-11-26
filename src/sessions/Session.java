package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import frames.AckFrame;
import frames.ConnectionFrame;
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
	
	public static final int MAX_CONNECTION_ATTEMPTS = 10;
    public static final int TIMEOUT_TIME = 3000; // milliseconds

	private ReceiveFrameBackgroundTask receiveFrameBackgroundTask = new ReceiveFrameBackgroundTask(this.network, this);
	protected Map<Byte, Long> sentFramesNumWithTimestamp = new HashMap<Byte, Long>();
	protected Queue<Frame> receptionQueue = new LinkedList<Frame>();
	protected IOException receptionException = null;
    
	protected NetworkAbstraction network;
	
	public Session(String machineName, int portNumber) throws UnknownHostException, IOException {
		this.network = new NetworkAbstraction(new Socket(machineName, portNumber));
		// Start the receiver background task for this network, notifying this session upon a frame reception
		this.receiveFrameBackgroundTask = new ReceiveFrameBackgroundTask(this.network, this);
		this.receiveFrameBackgroundTask.start();
	}

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
				try { Thread.sleep(10); } catch (Exception e) {/*Main thread cannot be killed */}
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

	// Since the protocol doesn't have a proper Disconnect Frame, we abruptly disconnect
	public boolean close() throws IOException {
		Log.verbose("Closing connection with host::" + network.getHostName() + "...");
		network.close();
		return true;
	}
        
	/*
<<<<<<< HEAD
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
				Log.println("Could not receive ACK for frame " + e.getFrame().getNum() + ": Resending");
			}
		}
=======
	public boolean attemptConnection(byte connectionType) throws IOException, InterruptedException, ExecutionException {
            
            try {
                ConnectionFrame cFrame = new ConnectionFrame(connectionType);

                ExecutorService executor;
                FutureTask fTask;
                do{
                    executor = Executors.newCachedThreadPool();
                    fTask = new FutureTask(new FrameSenderTask(network, cFrame)); // The SenderCallable object is the task to periodically execute.
                    executor.submit(fTask);

                    // Wait 3 seconds max. for the thread to terminate (with or
                    // without an error).
                    executor.awaitTermination(TIMER_UPPER_BOUND, TimeUnit.SECONDS);
                    System.out.println("Session says: \"End of the 'do while' loop in the 'attemptConnection' method.\"");
                }while(fTask.get(TIMER_UPPER_BOUND, TimeUnit.SECONDS) != null); // Reminder: the task (here, 'FrameSenderTask')throws an IOException if the
                                                                                // 'sent' is not completed and returns 'null' otherwise.
                System.out.println("Session says: \"Exited 'do while'.\"");
                
                Frame frame = network.receiveFrame();
                if (frame instanceof AckFrame){
                    return true;
                }
            } catch (TimeoutException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
	}
*/
}
