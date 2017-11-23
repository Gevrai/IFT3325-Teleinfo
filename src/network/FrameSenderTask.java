package network;

import java.io.IOException;
import java.util.concurrent.Callable;

import frames.Frame;

public class FrameSenderTask implements Callable<Void> {
	
	NetworkAbstraction network;
	Frame frame;

	public FrameSenderTask(NetworkAbstraction network, Frame frame) {
		this.network = network;
		this.frame = frame;
	}

	@Override
	public Void call() throws IOException {
		// Send a frame expecting a response before a Timer expires
		this.network.sendFrame(frame);
		return null;
	}
}
