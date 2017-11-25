package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import frames.AckFrame;
import frames.Frame;
import frames.InformationFrame;
import network.FrameSenderTask;

public class StopAndWaitSession extends Session {
	
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static int TIMEOUT = 3000;

	public StopAndWaitSession(String machineName, String portNumber) throws NumberFormatException, UnknownHostException, IOException {
		super(machineName, portNumber);
	}
	
	public ScheduledFuture<Void> setUpFrameSendingTask(Frame iframe) {
		FrameSenderTask task = new FrameSenderTask(network, iframe);
		task.
		return scheduler.schedule(task, TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean send(InputStream istream) throws IOException {
		byte[] buf = new byte[Frame.MAX_DATA_SIZE];
		int nbBytesRead;
		byte frameCount = 0;
		ScheduledFuture<Void> scheduledTask;
		
		while (istream.available() > 0) {
			// Send the frame every three seconds
			nbBytesRead = istream.read(buf);
			InformationFrame iframe = new InformationFrame(frameCount, Arrays.copyOf(buf, nbBytesRead));
			scheduledTask = setUpFrameSendingTask(iframe);
			scheduledTask.

			// Receive the frame, this will block until we get a valid frame
			while (!scheduledTask.isDone()) {
				Frame receivedFrame = network.receiveFrame();
				switch (receivedFrame.getType()) {
				case AckFrame.TYPE :
					scheduledTask.cancel(true);
					break;
				case RejFrame.TYPE :
				}



			// Verify the response is valid :
			// 		true  : stop the timer and continue
			// 		false : resend the frame
		}
			
		return false;
	}

}
