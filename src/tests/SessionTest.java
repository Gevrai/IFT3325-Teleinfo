package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import frames.ConnectionFrame;
import receiver.Receiver;
import sessions.Session;

class SessionTest {
    
	@Test
	void ConnectAndDisconnectTest() throws IOException {

		double receiverErrorRatio = 0.5;

		// Start a receiver in another thread
		Receiver receiver = new Receiver(0, receiverErrorRatio);
		Thread t = new Thread() {
			@Override
			public void run() { 
				try { 
					receiver.acceptConnections(); 
				} catch (IOException e) { 
					fail(); }}};
		t.start();

		// Try connecting and then unconnecting
		Session session = Session.connect("localhost", receiver.getLocalPort(), ConnectionFrame.STOP_AND_WAIT);
		assertTrue(session != null);
		
		session.close();
	}

}
