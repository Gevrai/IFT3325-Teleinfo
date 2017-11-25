package tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import sessions.GoBackNSession;
import sessions.Session;

/**
 *
 * @author Sebastien
 */
public class GoBackNSessionTest {
    public static final byte GO_BACK_N = 0;
    
    public static void main(String[] args) throws UnknownHostException, NumberFormatException, IOException, InterruptedException, ExecutionException{
        
        InetAddress ip;
        String hostname;
        ip = InetAddress.getLocalHost();
        hostname = ip.getHostName();
        
        ServerSocket ss = new ServerSocket(9090);
        
        Session gbns = new GoBackNSession(hostname, "9090");
        
        System.out.println("GoBackNSessionTest says: \"reached up to here.\"");
        
        gbns.attemptConnection(GO_BACK_N);
    }
}
