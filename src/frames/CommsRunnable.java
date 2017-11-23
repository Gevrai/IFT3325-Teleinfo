package frames;

import network.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public abstract class CommsRunnable implements Runnable{
    
    byte connectionType;
    ConnectionFrame cframe;
    NetworkAbstraction network;
}
