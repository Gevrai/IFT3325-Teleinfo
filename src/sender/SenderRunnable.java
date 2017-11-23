package sender;


import frames.CommsRunnable;
import frames.ConnectionFrame;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public class SenderRunnable extends CommsRunnable{
    
    ExecutorService executor;
    byte connectionType;
    ConnectionFrame cFrame;
    NetworkAbstraction network;
    
    @Override
    public void run(){
        try {
            network.sendFrame(cFrame);
        } catch (IOException ex) {
            Logger.getLogger(SenderRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
