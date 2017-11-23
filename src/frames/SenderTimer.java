package frames;


import frames.ConnectionFrame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.NetworkAbstraction;
import sender.CommsTimer;

/**
 *
 * @author Sebastien
 */
public class SenderTimer extends CommsTimer{
    
    byte connectionType;
    ConnectionFrame cFrame;
    NetworkAbstraction network;
    
    public SenderTimer(byte connectionType, ConnectionFrame cFrame, NetworkAbstraction network){
        super(connectionType, cFrame, network);
    }
    
    @Override
    public void run() {
        try {
            network.sendFrame(cFrame);
        } catch (IOException ex) {
            Logger.getLogger(SenderTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
