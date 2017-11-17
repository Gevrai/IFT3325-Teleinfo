package tests;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import frames.AckFrame;
import frames.ConnectionFrame;
import frames.EndConnectionFrame;
import frames.Frame;
import frames.InformationFrame;
import frames.MalformedFrameException;
import frames.PFrame;
import frames.RejFrame;
import utils.NetworkAbstraction;

public class NetworkAbstractionTest {

	@Test
	public void SendAndReceiveSingleFrameTest() throws IOException, MalformedFrameException {
		ServerSocket serverSocket = new ServerSocket(0);
		Socket senderSocket = new Socket("localhost", serverSocket.getLocalPort());
		Socket receiverSocket = serverSocket.accept();

		byte data[] = {5,3,6,3,52,26,73,86,111,63,75};
		Frame frameSent = new InformationFrame((byte) 0, data);
		
		NetworkAbstraction.sendFrame(frameSent, senderSocket.getOutputStream());
		Frame frameReceived = NetworkAbstraction.receiveFrame(receiverSocket.getInputStream());
		
		assertArrayEquals(frameSent.getBytes(), frameReceived.getBytes());
	}
	
	@Test
	public void SendAndReceiveMultipleFrameTest() throws IOException, MalformedFrameException {
		ServerSocket serverSocket = new ServerSocket(0);
		Socket senderSocket = new Socket("localhost", serverSocket.getLocalPort());
		Socket receiverSocket = serverSocket.accept();
		
		List<Frame> framesSent = new ArrayList<Frame>();
		List<Frame> framesReceived = new ArrayList<Frame>();

		framesSent.add(new ConnectionFrame(ConnectionFrame.GO_BACK_N));
		framesSent.add(new InformationFrame((byte) 0, new byte[]{35,4,62,64}));
		framesSent.add(new InformationFrame((byte) 1, new byte[]{3,45,2,14}));
		framesSent.add(new InformationFrame((byte) 2, new byte[]{35,4,64,74,27,35,37,85,27,35,84,34,73}));
		framesSent.add(new AckFrame((byte) 0));
		framesSent.add(new RejFrame((byte) 1));
		framesSent.add(new EndConnectionFrame());
		framesSent.add(new PFrame());
		
		// Interlacing for fun, but kind of a test ?
		NetworkAbstraction.sendFrame(framesSent.get(0), senderSocket.getOutputStream());
		NetworkAbstraction.sendFrame(framesSent.get(1), senderSocket.getOutputStream());
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		NetworkAbstraction.sendFrame(framesSent.get(2), senderSocket.getOutputStream());
		NetworkAbstraction.sendFrame(framesSent.get(3), senderSocket.getOutputStream());
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		NetworkAbstraction.sendFrame(framesSent.get(4), senderSocket.getOutputStream());
		NetworkAbstraction.sendFrame(framesSent.get(5), senderSocket.getOutputStream());
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		NetworkAbstraction.sendFrame(framesSent.get(6), senderSocket.getOutputStream());
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		NetworkAbstraction.sendFrame(framesSent.get(7), senderSocket.getOutputStream());
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		framesReceived.add(NetworkAbstraction.receiveFrame(receiverSocket.getInputStream()));
		
		for(int i=0 ; i <framesSent.size() ; i++) {
			assertArrayEquals(framesSent.get(i).getBytes(), framesReceived.get(i).getBytes());
		}
	}
	
}
