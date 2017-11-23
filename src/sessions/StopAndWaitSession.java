package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;

import frames.Frame;
import frames.InformationFrame;

public class StopAndWaitSession extends Session {

	public StopAndWaitSession(String machineName, String portNumber) throws NumberFormatException, UnknownHostException, IOException {
		super(machineName, portNumber);
	}

	@Override
	public boolean send(InputStream istream) throws IOException {
		byte[] buf = new byte[Frame.MAX_DATA_SIZE];
		int nbBytesRead;
		byte frameCount = 0;
		
		while (istream.available() > 0) {
			nbBytesRead = istream.read(buf);
			InformationFrame iframe = new InformationFrame(frameCount, Arrays.copyOf(buf, nbBytesRead));
			// Send a frame expecting a response before a Timer expires
			network.sendFrame(iframe);

			// Receive the frame
			// Verify the response is valid :
			// 		true  : stop the timer and continue
			// 		false : resend the frame
		}
			
		return false;
	}

}
