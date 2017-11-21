package sender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import frames.ConversionUtils;
import frames.Frame;
import frames.FrameFactory;
import frames.MalformedFrameException;
import network.NetworkAbstraction;
import tests.StopAndWaitClientTests;

public class StopAndWaitSender extends Sender{
    public static final byte STOP_AND_WAIT = 2;
    
    public StopAndWaitSender(String machineName, String portNumber, String fileName, String goBackAmount) {
        super(machineName, portNumber, fileName, goBackAmount);
    }
    
    // Sends all data from input stream 
    public void sendData(InputStream inputStream) throws IOException {
    	InputStream i = new InputStream();
    	i.read(arg0);

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
