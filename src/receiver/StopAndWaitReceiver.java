package receiver;

import java.io.IOException;

public class StopAndWaitReceiver extends Receiver{
    public static final byte STOP_AND_WAIT = 2;
    
    public StopAndWaitReceiver(int portNumber) throws IOException {
        super(portNumber);
    }

    protected byte[] receiveData() throws IOException {

        Frame frame;
        try {
            NetworkAbstraction.receiveFrame(dataInput);
        } catch (MalformedFrameException ex) {
            Logger.getLogger(StopAndWaitReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public static void main(String[] args){
		try {
			StopAndWaitReceiver sawReceiver = new StopAndWaitReceiver(PORT_NUMBER);
			sawReceiver.run();
		} catch (IOException ex) {
			Logger.getLogger(StopAndWaitServerTests.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
