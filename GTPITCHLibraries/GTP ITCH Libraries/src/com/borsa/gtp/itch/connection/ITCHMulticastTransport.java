package com.borsa.gtp.itch.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class ITCHMulticastTransport 
{
	private static int tPort = 4000;
	private static String tHost = "255.0.0.1";
	private static String tIf = "eth0";

	public static void main(String[] pArgs) throws IOException {

		
		  
        for (int i = 0; i < pArgs.length; i++) {
        	
        	
            if (pArgs[i].equals("-port") && i < (pArgs.length - 1)) {
                try {
                    tPort = Integer.parseInt(pArgs[i + 1]);
                }
                catch (NumberFormatException e) {
                }
            }
            
            if (pArgs[i].equals("-host") && i < (pArgs.length - 1)) {
                tHost = pArgs[i + 1];
            }
                        
            if (pArgs[i].equals("-if") && i < (pArgs.length - 1)) {
                tIf  = pArgs[i + 1];
            }
            
            if (pArgs[i].equals("-help")||
            	pArgs[i].equals("-h") ||
            	pArgs[i].equals("-?"))
            {            	
                ShowUsage();
                System.exit(0);
            }      
        }
        
		// join the multicast group
		MulticastSocket s = new MulticastSocket(tPort);
		SocketAddress join_addr=new InetSocketAddress(tHost, tPort);
		s.joinGroup(join_addr,NetworkInterface.getByName(tIf));
		// Now the socket is set up and we are ready to receive packets
		// Create a DatagramPacket and do a receive
		byte buf[] = new byte[1024];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		s.receive(pack);
		// Finally, let us do something useful with the data we just received,
		// like print it on stdout :-)
		System.out.println("Received data from: " + pack.getAddress().toString() +
				    ":" + pack.getPort() + " with length: " +
				    pack.getLength());
		System.out.write(pack.getData(),0,pack.getLength());
		System.out.println();
		// And when we have finished receiving data leave the multicast group and
		// close the socket
//		s.leaveGroup(join_addr,NetworkInterface.getByName(args[3]);
//		s.close();

	}
	
    private static void ShowUsage() {
		
    	System.out.println("Usage:");    	
    	System.out.println("-port <port>\t\t Connect to TCP port <port> (default: 4000)");
    	System.out.println("-host <host>\t\t Connect to TCP IP host <host> (default: 255.0.0.1)");    	
    	System.out.println("-if <id>\t\t Interface Id (default: 'eth0')");    	
		
	}
}
