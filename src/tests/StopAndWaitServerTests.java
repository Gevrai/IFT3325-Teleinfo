package tests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import receiver.StopAndWaitReceiver;

/**
 *
 * @author Sebastien
 */
public class StopAndWaitServerTests {
        private static final int PORT_NUMBER = 9000;
        public static void main(String[] args){
            try {
                StopAndWaitReceiver sawReceiver = new StopAndWaitReceiver(PORT_NUMBER);
                sawReceiver.run();
            } catch (IOException ex) {
                Logger.getLogger(StopAndWaitServerTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}
