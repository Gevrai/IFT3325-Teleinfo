package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;

import frames.AckFrame;
import frames.Frame;
import frames.InformationFrame;
import frames.RejFrame;
import utils.Log;
import utils.NumWindow;

public class GoBackNSession extends Session {

	public GoBackNSession(String machineName, int portNumber) throws UnknownHostException, IOException {
		super(machineName, portNumber);
	}

	@Override
	public boolean send(InputStream istream) throws IOException {
		byte[] buf = new byte[Frame.MAX_DATA_SIZE];
		int nbBytesRead;
		NumWindow sendingWindow = new NumWindow(Frame.MAX_NUM, Frame.MAX_NUM - 1);
		byte currentFrameNum = 0;
		Frame sentFrame, receivedFrame;
		
		// As long as there is still data to send
		while (istream.available() > 0 || !sendingWindow.isEmpty()) {

			// Check if we need to send anything
			while (sendingWindow.hasNextToSend()) {
				Frame toSend = sendingWindow.getNextToSendAndStamp();
				Log.println("Sending frame " + toSend.getNum());
				network.sendFrame(toSend);
			}

			// While there is place in window, place a frame in it
			while (sendingWindow.canPut(currentFrameNum) && istream.available() > 0) {
				nbBytesRead = istream.read(buf);
				sentFrame = new InformationFrame(currentFrameNum, Arrays.copyOf(buf, nbBytesRead));
				sendingWindow.put(sentFrame);
				currentFrameNum = (byte) ((currentFrameNum+1)%Frame.MAX_NUM);
			}

			// Process received frames
			if (this.receptionQueue.isEmpty())
				try { Thread.sleep(2); } catch (Exception e) {}
			while ((receivedFrame = this.receptionQueue.poll()) != null) {
				switch (receivedFrame.getType()) {
				case AckFrame.TYPE :
					Log.println("Got ack frame for " + receivedFrame.getNum());
					sendingWindow.ackUpTo(receivedFrame.getNum());
					break;
				case RejFrame.TYPE :
					sendingWindow.goBackTo(receivedFrame.getNum());
					break;
				default :
					Log.verbose("Sender got unusual frame...");
				}
			}

			// If first frame in window expired, reset there
			if (sendingWindow.hasFirstExpired(TIMEOUT_TIME)) {
				Log.verbose("Sender expired while waiting for " + sendingWindow.getCurrentFirstAck());
				sendingWindow.goBackTo(sendingWindow.getCurrentFirstAck());
			}
		}

		return true;
	}


}
