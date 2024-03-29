package tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import frames.ConnectionFrame;
import receiver.Receiver;
import sessions.Session;
import utils.Log;

public class SessionTest {
    
	@Test
	public void ConnectAndDisconnectTest() throws IOException {

		double receiverErrorRatio = 0.8;
		Log.setVerbose(false);

		// Start a receiver in another thread
		Receiver receiver = new Receiver(0, receiverErrorRatio, null);
		Thread t = new Thread() { @Override public void run() { receiver.acceptConnectionAndProcess();}};
		t.start();

		// Try connecting and then unconnecting
		Session.setTimeout(20);
		Session session = Session.connect("localhost", receiver.getLocalPort(), ConnectionFrame.STOP_AND_WAIT);
		assertTrue(session != null);
		
		// Finish
		session.close();
	}

	@Test
	public void StopAndWaitSendTextTest() throws IOException {
		
		double receiverErrorRatio = 0.8;
		Log.setVerbose(false);

		// Start a receiver in another thread
		Receiver receiver = new Receiver(0, receiverErrorRatio, null);
		Thread t = new Thread() { @Override public void run() { receiver.acceptConnectionAndProcess();}};
		t.start();
		
		// Connect a session to this receiver
		Session.setTimeout(20);
		Session session = Session.connect("localhost", receiver.getLocalPort(), ConnectionFrame.STOP_AND_WAIT);
		assertTrue(session != null);
		
		// Set up receiver's data output
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		receiver.getReceiverWorker().setOuputStream(ostream);
		
		// Send a string
		String test = "123456789abcdef123456789abcdef123456789abcdef123456789abcdef123456789abcdef123456789abcde123456789abcdefabcdef123456789abcdef123456789abcdef123456789abcde123456789abcdef";
		InputStream istream = new ByteArrayInputStream(test.getBytes());
		session.send(istream);
		
		// Compare reception and original string
		String received = new String(ostream.toByteArray());
		assertEquals(test, received);
		assertArrayEquals(test.getBytes(), ostream.toByteArray());
		
		// Finish
		session.close();
	}

	@Test
	public void GoBackNSendTextTest() throws IOException {
		
		double receiverErrorRatio = 0.5;
		Log.setVerbose(false);

		// Start a receiver in another thread
		Receiver receiver = new Receiver(0, receiverErrorRatio, null);
		Thread t = new Thread() { @Override public void run() { receiver.acceptConnectionAndProcess();}};
		t.start();
		
		// Connect a session to this receiver
		Session.setTimeout(20);
		Session session = Session.connect("localhost", receiver.getLocalPort(), ConnectionFrame.GO_BACK_N);
		assertTrue(session != null);
		
		// Set up receiver's data output
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		receiver.getReceiverWorker().setOuputStream(ostream);
		
		// Send a string
		String test = "123456789abcdef123456789abcdef123456789abcdef123456789abcdef123456789abcdef123456789abcde123456789abcdefabcdef123456789abcdef123456789abcdef123456789abcde123456789abcdef";
		InputStream istream = new ByteArrayInputStream(test.getBytes());
		session.send(istream);
		
		// Compare reception and original string
		String received = new String(ostream.toByteArray());
		assertEquals(test, received);
		assertArrayEquals(test.getBytes(), ostream.toByteArray());
		
		// Finish
		session.close();
	}
}
