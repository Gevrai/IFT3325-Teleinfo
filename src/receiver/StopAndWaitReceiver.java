package receiver;

import frames.Frame;
import frames.FrameFactory;
import frames.MalformedFrameException;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public class StopAndWaitReceiver extends Receiver{
    public static final byte STOP_AND_WAIT = 2;
    
    public StopAndWaitReceiver(int portNumber) throws IOException {
        super(portNumber);
    }
    /*
    protected void receiveData(DataInputStream dataInput) throws IOException {
        Frame frame;
        try {
            NetworkAbstraction.receiveFrame(dataInput);
        } catch (MalformedFrameException ex) {
            Logger.getLogger(StopAndWaitReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
}
