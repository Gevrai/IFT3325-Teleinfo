package network;

import java.io.IOException;

/** Thread that is only responsible of reception of a frame from a NetworkAbstraction and
 *  notifying a IFrameReceiver.
 *
 */
public class ReceiveFrameBackgroundTask extends Thread {
	
	private NetworkAbstraction network;
	private IFrameReceiver mainReceiver;
	
	public ReceiveFrameBackgroundTask(NetworkAbstraction network, IFrameReceiver mainReceiver) {
		this.network = network;
		this.mainReceiver = mainReceiver;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				this.mainReceiver.notifyFrameReceived(network.receiveFrame());
			}
		} catch (IOException e) {
			this.mainReceiver.notifyIOException(e);
		}
	}

}
