package tests;

import frames.ConnectionFrame;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import receiver.Receiver;
import sessions.Session;
import sessions.StopAndWaitSession;

/**
 *
 * @author Sebastien
 */
public class StopAndWaitSessionTest {
    
    public static void main(String[] args) throws UnknownHostException, NumberFormatException, IOException, InterruptedException, ExecutionException{
        double receiverErrorRatio = 0.5;
        
        InetAddress ip;
        String hostname;
        ip = InetAddress.getLocalHost();
        hostname = ip.getHostName();
        
        ServerSocket ss = new ServerSocket(9090);
        Socket soc = new Socket();
        
        Session saw = new StopAndWaitSession(hostname, 9090);
        
        // Start a receiver in another thread
        Receiver receiver = new Receiver(0, receiverErrorRatio);
        Thread t = new Thread() {
            @Override
            public void run() { 
                try { 
                    receiver.acceptConnections(); 
                } catch (IOException e) { 
                    fail(); }}};
        t.start();
        
        System.out.println("StopAndWaitSessionTest says: \"Gonna attempt a connection now.\"");
        Session session = Session.connect("localhost", receiver.getLocalPort(), ConnectionFrame.STOP_AND_WAIT);
        assertTrue(session != null);
        System.out.println("StopAndWaitSessionTest says: \"Gonna close the connection now.\"");
        session.close();
    }
}
