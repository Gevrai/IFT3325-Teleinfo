package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;

import frames.AckFrame;
import frames.Frame;
import frames.InformationFrame;
import utils.Log;

public class StopAndWaitSession extends Session {

	public StopAndWaitSession(String machineName, int portNumber) throws UnknownHostException, IOException {
		super(machineName, portNumber);
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
			Log.verbose("Sending InformationFrame::" + frameCount);

			// If we did not receive the ACK frame before a set ammount of time, resend
			boolean isAck = false;
			while(!isAck) {
				long timeSent = System.currentTimeMillis();
				while (timeSent + Session.TIMEOUT_TIME > System.currentTimeMillis()) {
					// Throw exception from thread if there was one
					if (this.receptionException != null) 
						throw receptionException;
					if (receptionQueue.isEmpty())
						// Check every 10ms to avoid spinning
						try { Thread.sleep(10); } catch (Exception e) {/*Main thread cannot be killed */}
					// Verify the response is valid :
					else {
						Frame receivedFrame = receptionQueue.poll();
						// Anything else than ACK of right num we restart
						if (receivedFrame.getType() == AckFrame.TYPE && receivedFrame.getNum() == iframe.getNum()) {
							// YAY, go to next one!
							Log.verbose("Received AckFrame::" + frameCount);
							isAck = true;
							break;
						} else {
							Log.verbose("Received invalid response to InformationFrame::" + frameCount);
						}
					}
					Log.verbose("Did not receive response in time for InformationFrame::" + frameCount +"... Resending...");
				}
			}
			frameCount = (byte) ((frameCount+1)%Frame.MAX_NUM);
		}
		Log.verbose("Successfully sent InputStream");
		return true;
	}
}
