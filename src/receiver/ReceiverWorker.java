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

public class ReceiverWorker implements IFrameReceiver {
	
	private NetworkAbstraction network;

	private byte connectionType;
	private boolean isConnectionEstablished = false;
	
	private OutputStream ostream;

	private ReceiveFrameBackgroundTask receiverFrameBackgroundTask;
	private Queue<Frame> receptionQueue = new LinkedList<Frame>();
	private IOException receptionException = null;
	
	public ReceiverWorker(Socket clientSocket, double errorRatio) throws IOException {
		this.network = new NetworkAbstraction(clientSocket, errorRatio);
		this.receiverFrameBackgroundTask = new ReceiveFrameBackgroundTask(network, this);
		this.ostream = System.out;
	}
	
	public void setOuputStream(OutputStream ostream) { this.ostream = ostream; }
	public OutputStream getOuputStream() {return this.ostream; }
	
	public void startProcessing() {
		this.receiverFrameBackgroundTask.start();

		try {
			// Process the frames as they arrive
			while (true) {
				// IOException in receiver background task
				if (receptionException != null) {
					throw receptionException;
				}
				// Is there a new frame to process ?
				if (receptionQueue.isEmpty())
					try { Thread.sleep(1); } catch (Exception e) {/* Don't care if it's killed while sleeping */}
				else {
					Frame receivedFrame = receptionQueue.poll();
					switch (receivedFrame.getType()) {
					case ConnectionFrame.TYPE :
						processConnectionFrame((ConnectionFrame) receivedFrame);
						break;
					case InformationFrame.TYPE :
						processInformationFrame((InformationFrame) receivedFrame);
						break;
					case PollFrame.TYPE :
						processPoll();
						break;
					case FinalFrame.TYPE :
						disconnect();
						return;
					default :
						Log.verbose("Received a unusual frame, ignoring...");
						break;
					}
				}
			}
		} catch (IOException e) { }

		this.receiverFrameBackgroundTask.interrupt();
	}

	
	private void disconnect() throws IOException {
		Log.verbose("Received FinalFrame : Disconnecting");
		Frame ackFrame = new AckFrame((byte) 0);
		this.network.sendFrame(ackFrame);
		ostream.flush();
	}

	// With this, it could be possible to change connection type by simply reasking for a new connection
	private void processConnectionFrame(ConnectionFrame cframe) throws IOException {
		// Check connection type is valid and send response
		this.connectionType = cframe.getNum();

		// Set up num window 
		switch (connectionType) {
			case ConnectionFrame.GO_BACK_N :
			case ConnectionFrame.STOP_AND_WAIT :
				Log.verbose("Received ConnectionFrame::" + cframe.getNum() + " ... Accepting connection");
				break;
			case ConnectionFrame.SELECTIVE_REJECT :
				throw new UnsupportedOperationException("SELECTIVE_REJECT not implemented");
			default :
				Log.verbose("Unknown connection type received...");
				Frame rejFrame = new RejFrame((byte) 0);
				this.network.sendFrame(rejFrame);
				this.isConnectionEstablished = false;
				return;
		}
		
		Frame ackFrame = new AckFrame((byte) 0);
		this.network.sendFrame(ackFrame);
		this.isConnectionEstablished = true;
	}
	
	byte currentNum = 0;
	int test = 1;
	private void processInformationFrame(InformationFrame iframe) throws IOException {
		// Drop the frame if there was no connection formally established
		if (!isConnectionEstablished)
			return;

		// Process the frame depending on current protocol
		switch (this.connectionType) {
		case ConnectionFrame.GO_BACK_N :
		case ConnectionFrame.STOP_AND_WAIT :
			if (iframe.getNum() == currentNum) {
				// Write data, ACK the frame and wait for next one!
				this.ostream.write(iframe.getData());
				network.sendFrame(new AckFrame(currentNum));
				Log.verbose("Received InformationFrame::" + iframe.getNum() + " ... Accepting");
				currentNum = (byte) ((currentNum + 1) % Frame.MAX_NUM);
			} else {
				// If we receive the wrong frame (not in order), NAK with expected frame
				// Note that this implicitly tells the sender what frames, if any, were correctly received before
				Log.verbose("Received InformationFrame::" + iframe.getNum() + " (was expecting " + currentNum + ") ... Rejecting");
				network.sendFrame(new RejFrame(currentNum));
			}
			return;
		case ConnectionFrame.SELECTIVE_REJECT :
			throw new UnsupportedOperationException("SELECTIVE_REJECT not implemented");
		}
	}

	private void processPoll() throws IOException {
		switch (this.connectionType) {
		case ConnectionFrame.STOP_AND_WAIT :
		case ConnectionFrame.GO_BACK_N :
			// Tell sender we are waiting for 'currentNum' frame
			this.network.sendFrame(new RejFrame(currentNum));
			Log.verbose("Received PollFrame: Tellin sender what frame we are expecting");
			return;
		case ConnectionFrame.SELECTIVE_REJECT :
			throw new UnsupportedOperationException("SELECTIVE_REJECT not implemented");
		}
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
