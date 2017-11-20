package tests;

import frames.ConversionUtils;
import frames.MalformedFrameException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import receiver.StopAndWaitReceiver;
import sender.StopAndWaitSender;
import utils.NetworkAbstraction;

/**
 *
 * @author Sebastien
 */
public class StopAndWaitClientTests {
    private static final int PORT_NUMBER = 9000;
    private static final String TEST_FILE = "testFile";
    
    public static void main(String[] args){
        System.setProperty("java.net.preferIPv4Stack" , "true"); // To force Java to use IPv4.
        try {
            // Initialize arguments required for testing -- both sender and
            // receiver are operating on the local host.
            String hostName = "unknown host";
            InetAddress addr;
            addr = InetAddress.getByName("localhost");
            hostName = addr.getHostName();
            
            //ServerSocket serverSocket = new ServerSocket(PORT_NUMBER); // Server socket.
            
            StopAndWaitSender sawSender = new StopAndWaitSender(hostName, String.valueOf(PORT_NUMBER), TEST_FILE, "0");
            //StopAndWaitReceiver sawReceiver = new StopAndWaitReceiver(PORT_NUMBER);
            
            Socket senderSocket = new Socket(sawSender.getMachineName(), sawSender.getPortNumber()); // Sender socket.
            //Socket receiverSocket = serverSocket.accept(); // Receiver socket.
            
            // Streams for sender:
            DataOutputStream dOSenderStream = new DataOutputStream(senderSocket.getOutputStream());
            DataInputStream dISenderStream = new DataInputStream(senderSocket.getInputStream());
            
            
            // Send the data streams through the sockets.
            
            // 1. Send connection frame.
            //dOSenderStream.write(ConversionUtils.charsToBytes(new char[]{'C','0'}));
            sawSender.sendData(dOSenderStream, ConversionUtils.charsToBytes(new char[]{'C','0'}));
            
            try {
                // 2. Receive ACK:
                System.out.println(NetworkAbstraction.receiveFrame(dISenderStream));
            } catch (MalformedFrameException ex){
                Logger.getLogger(StopAndWaitClientTests.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
}