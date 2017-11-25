package network;

import java.io.IOException;

import frames.Frame;

public interface IFrameReceiver {
	
	public void notifyFrameReceived(Frame frame);

	public void notifyIOException(IOException e);
}
