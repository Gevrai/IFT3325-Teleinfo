import frames.Frame;
import receiver.Receiver;
import sender.Sender;
import sessions.Session;
import utils.Log;

public class SendAndReceiveTest {

	// Simple case test, this would normally be run on different command line, but it is equivalent 
	// to do it on two threads to test
	public static void main(String args[]) {
		
		// Setup test
		Log.setVerbose(true);
		// 30% failure in sending and receiving frames, and outputs to file 'output.txt'
		String[] receiverArgs = {"5565", "0.3", "output.txt"};
		// Reads and send file 'testInput.txt'
		String[] senderArgs = {"localhost", "5565", "testInput.txt", "0"};
		// Frame are maximum 100 bytes
		Frame.setMaxFrameSize(100);
		// Timeout of 3 seconds
		Session.setTimeout(3000);
		// Receiver running in a thread (simulating another command line)
		Thread t = new Thread() { @Override public void run() { Receiver.main(receiverArgs); }};
		t.start();
		// Start sender
		Sender.main(senderArgs);
		
		t.interrupt();
	}
}