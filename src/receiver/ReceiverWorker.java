package receiver;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.Frame;
import network.IFrameReceiver;
import network.NetworkAbstraction;
import network.ReceiveFrameBackgroundTask;
import utils.Log;

public class ReceiverWorker extends Thread implements IFrameReceiver {
	
	private ReceiveFrameBackgroundTask receiverFrameBackgroundTask;
	private Queue<Frame> receptionQueue = new LinkedList<Frame>();
	private NetworkAbstraction network;
	private IOException receptionException = null;
	
	public ReceiverWorker(Socket clientSocket, double errorRatio) throws IOException {
		this.network = new NetworkAbstraction(clientSocket, errorRatio);
		this.receiverFrameBackgroundTask = new ReceiveFrameBackgroundTask(network, this);
		this.receiverFrameBackgroundTask.start();
	}
	
	@Override
	public void run() {
		try {
		// Process the frames as they arrive
		while (true) {
			// IOException in receiver background task
			if (receptionException != null) {
				return;
			}
			// Is there a new frame to process ?
			if (receptionQueue.isEmpty())
				try { Thread.sleep(10); } catch (Exception e) {/* Don't care if it's killed while sleeping */}
			else {
				Frame receivedFrame = receptionQueue.poll();
				switch (receivedFrame.getType()) {
				case ConnectionFrame.TYPE :
					acceptConnection(); break;
				default :
					// TODO all
					throw new UnsupportedOperationException();
				}
			}
		}
		} catch (IOException e) {
			Log.println("client::" + network.getHostName() + " disconnected...");
		}
	}
	
	private void acceptConnection() throws IOException {
		// Send confirmation
		Frame ackFrame = new AckFrame((byte) 0);
		this.network.sendFrame(ackFrame);

	}

	@Override
	public void notifyFrameReceived(Frame frame) {
		this.receptionQueue.add(frame);
	}
	
	@Override
	public void notifyIOException(IOException e) {
		// Tell main thread there was an IOException on this network
		this.receptionException = e;
	}
}
