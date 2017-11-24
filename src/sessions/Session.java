package sessions;

import frames.AckFrame;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import frames.ConnectionFrame;
import frames.Frame;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.lang.Exception;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.FrameSenderTask;
import network.NetworkAbstraction;
import sender.SenderCallable;

public abstract class Session {
	
    private final int TIMER_UPPER_BOUND = 3; // seconds
    
	private Socket socket;
	protected NetworkAbstraction network;
	
	public Session(String machineName, String portNumber) throws NumberFormatException, UnknownHostException, IOException {
		Socket socket = new Socket(machineName, Integer.parseUnsignedInt(portNumber));
		this.network = new NetworkAbstraction(socket);
	}

	public static Session connect(String machineName, String portNumber, String connectionType) throws InterruptedException, ExecutionException {
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
        
	public abstract boolean send(InputStream istream) throws IOException;

	// Since the protocol doesn't have a proper Disconnect Frame, we abruptly disconnect
	public boolean close() throws IOException {
            socket.close();
            return true;
	}
        
}
