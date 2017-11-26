package receiver;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.FinalFrame;
import frames.Frame;
import frames.InformationFrame;
import frames.PollFrame;
import frames.RejFrame;
import network.IFrameReceiver;
import network.NetworkAbstraction;
import network.ReceiveFrameBackgroundTask;
import utils.Log;
import utils.NumWindow;

public class ReceiverWorker extends Thread implements IFrameReceiver {
	
	private NetworkAbstraction network;

	private byte connectionType;
	private boolean isConnectionEstablished = false;
	
	private OutputStream ostream = System.out;

	private ReceiveFrameBackgroundTask receiverFrameBackgroundTask;
	private Queue<Frame> receptionQueue = new LinkedList<Frame>();
	private IOException receptionException = null;

	private NumWindow window;
	
	public ReceiverWorker(Socket clientSocket, double errorRatio) throws IOException {
		this.network = new NetworkAbstraction(clientSocket, errorRatio);
		this.receiverFrameBackgroundTask = new ReceiveFrameBackgroundTask(network, this);
		this.receiverFrameBackgroundTask.start();
	}
	
	public void setOuputStream(OutputStream ostream) { this.ostream = ostream; }
	public OutputStream getOuputStream() {return this.ostream; }
	
	@Override
	public void run() {
		try {
		// Process the frames as they arrive
		while (true) {
			// IOException in receiver background task
			if (receptionException != null) {
				throw receptionException;
			}
			// Is there a new frame to process ?
			if (receptionQueue.isEmpty())
				try { Thread.sleep(10); } catch (Exception e) {/* Don't care if it's killed while sleeping */}
			else {
				Frame receivedFrame = receptionQueue.poll();
				switch (receivedFrame.getType()) {
				case ConnectionFrame.TYPE :
					processConnectionFrame((ConnectionFrame) receivedFrame); break;
				case InformationFrame.TYPE :
					processInformationFrame((InformationFrame) receivedFrame); break;
				case PollFrame.TYPE :
					processPoll(); break;
				case FinalFrame.TYPE :
					disconnect(); break;
				default :
					Log.verbose("Received a unusual frame, ignoring..."); break;
				}
			}
		}
		} catch (IOException e) {
			Log.println("client::" + network.getHostName() + " produced and IOException, disconnecting...");
			return;
		}
	}
	
	private void disconnect() throws IOException {
		Frame ackFrame = new AckFrame((byte) 0);
		this.network.sendFrame(ackFrame);
		ostream.close();
		network.close();
	}

	private void processPoll() throws IOException {
		switch (this.connectionType) {
		case ConnectionFrame.SELECTIVE_REJECT :
			RejFrame[] rejs = window.getSelectiveRejects();
			for (RejFrame rej : rejs) this.network.sendFrame(rej);
			return;
		case ConnectionFrame.GO_BACK_N :
			this.network.sendFrame(new RejFrame(window.getCurrentNum()));
			return;
		default :
			return;
		}
	}

	// With this, it could be possible to change connection type by simply reasking for a new connection
	private void processConnectionFrame(ConnectionFrame cframe) throws IOException {
		// Check connection type is valid and send response
		this.connectionType = cframe.getNum();

		// Set up num window 
		int currentNum = window == null ? 0 : window.currentFirst;
		switch (connectionType) {
			case ConnectionFrame.GO_BACK_N : // 2^n - 1
				this.window = new NumWindow(Frame.MAX_NUM, Frame.MAX_NUM - 1);
				break;
			case ConnectionFrame.SELECTIVE_REJECT : // 2^(n-1)
				this.window = new NumWindow(Frame.MAX_NUM, Frame.MAX_NUM / 2);
				break;
			case ConnectionFrame.STOP_AND_WAIT :
				this.window = new NumWindow(Frame.MAX_NUM, 1); // Single frame window
				break;
			default :
				Log.verbose("Unknown connection type received...");
				Frame rejFrame = new RejFrame((byte) 0);
				this.network.sendFrame(rejFrame);
				this.isConnectionEstablished = false;
				return;
		}

		window.reset(currentNum);
		
		Frame ackFrame = new AckFrame((byte) 0);
		this.network.sendFrame(ackFrame);
		this.isConnectionEstablished = true;
	}
	
	private void processInformationFrame(InformationFrame iframe) throws IOException {
		// Drop the frame if there was not connection frame sent
		if (!isConnectionEstablished)
			return;
		// Check if valid with current window, else drop
		if(!window.put(iframe))
			return;
		// Something to process ?
		if (!window.hasNext())
			return;
		while (window.hasNext())
			this.ostream.write(window.popNext().getData());
		// Send ACK frame up until current num
		network.sendFrame(new AckFrame(window.getCurrentNum()));
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
