package frames;

import java.util.concurrent.Callable;
import network.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public abstract class CommsCallable implements Callable{
    
    byte connectionType;
    ConnectionFrame cframe;
    NetworkAbstraction network;
    
    public CommsCallable(byte ct, ConnectionFrame cf, NetworkAbstraction na){
        this.connectionType = ct;
        this.cframe = cf;
        this.network = na;
    }
}
