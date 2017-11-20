package sender;

import frames.Frame;
import frames.FrameFactory;
import frames.MalformedFrameException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public class StopAndWaitSender extends Sender{
    public static final byte STOP_AND_WAIT = 2;
    
    public StopAndWaitSender(String machineName, String portNumber, String fileName, String goBackAmount) {
        super(machineName, portNumber, fileName, goBackAmount);
    }
    
    public void sendData(DataOutputStream dataOutput, byte[] frameBytes) throws IOException {
        try{
            Frame frame;
            try {
                frame = FrameFactory.fromBytes(frameBytes);
                NetworkAbstraction.sendFrame(frame, dataOutput);
            } catch (MalformedFrameException ex) {
                Logger.getLogger(StopAndWaitSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
}
