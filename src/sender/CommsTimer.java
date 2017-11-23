package sender;

import frames.ConnectionFrame;
import java.util.TimerTask;
import network.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public abstract class CommsTimer extends TimerTask{
    
    byte connectionType;
    ConnectionFrame cframe;
    NetworkAbstraction network;
    
    public CommsTimer(byte connectionType, ConnectionFrame cFrame, NetworkAbstraction network){
        this.connectionType = connectionType;
	this.cframe = new ConnectionFrame(connectionType);
        this.network = network;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
