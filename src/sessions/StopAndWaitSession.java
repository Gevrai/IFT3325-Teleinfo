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

public class StopAndWaitSession extends Session {

	public StopAndWaitSession(String machineName, int portNumber) throws UnknownHostException, IOException {
		super(machineName, portNumber);
	}
	
	public void sendAndAwaitResponse(InformationFrame iframe) throws IOException {
		// Send the frame while we did not receive an ACK
		while (true) {
			Log.verbose("Sending InformationFrame::" + iframe.getNum());
			network.sendFrame(iframe);
			// If we did not receive the ACK frame before a set ammount of time, resend
			long timeSent = System.currentTimeMillis();
			while (timeSent + Session.TIMEOUT_TIME > System.currentTimeMillis()) {
				// Throw exception from thread if there was one
				if (this.receptionException != null) 
					throw receptionException;
				// New frame to process ?
				if (receptionQueue.isEmpty())
					// Check every 10ms to avoid spinning
					try { Thread.sleep(10); } catch (Exception e) {/*Main thread cannot be killed */}
				else {
					// Verify the response is valid ? ACK with right num : resend
					Frame receivedFrame = receptionQueue.poll();
					if (receivedFrame.getType() == AckFrame.TYPE && receivedFrame.getNum() == iframe.getNum()) {
						Log.verbose("Received AckFrame::" + iframe.getNum());
						return;
					} 
					// Receiver was expecting next frame -> implicit ACK of previous
					if (receivedFrame.getType() == RejFrame.TYPE && receivedFrame.getNum() == (iframe.getNum()+1)%Frame.MAX_NUM) {
						Log.verbose("Received implicit AckFrame::" + iframe.getNum());
						return;
					}
					// Else not a valid response...
					Log.verbose("Received invalid response to InformationFrame::" + iframe.getNum());
				}
			}

			Log.verbose("Did not receive response in time for InformationFrame::" + iframe.getNum() +"... Resending...");
		}
	}
	
	@Override
	public boolean send(InputStream istream) throws IOException {
		byte[] buf = new byte[Frame.MAX_DATA_SIZE];
		int nbBytesRead;
		byte frameCount = 0;
		
		Log.verbose("Sending content of InputStream...");
		while (istream.available() > 0) {
			// Construct the frame to send
			nbBytesRead = istream.read(buf);
			InformationFrame iframe = new InformationFrame(frameCount, Arrays.copyOf(buf, nbBytesRead));
			sendAndAwaitResponse(iframe);
			frameCount = (byte) ((frameCount+1)%Frame.MAX_NUM);
		}
		Log.verbose("Successfully sent InputStream");
		return true;
	}
}
