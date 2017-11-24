package sender;


import frames.CommsCallable;
import frames.ConnectionFrame;
import java.io.IOException;
import network.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public class SenderCallable extends CommsCallable{
    
    byte connectionType;
    ConnectionFrame cFrame;
    NetworkAbstraction network;
    
    public SenderCallable(byte connectionType, ConnectionFrame cFrame, NetworkAbstraction network) {
        super(connectionType, cFrame, network);
    }
    
    @Override
    public Void call() throws IOException {
        network.sendFrame(cFrame);
        
        return null; // Task terminated.
    }
}
