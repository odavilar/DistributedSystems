/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author uidp1416
 */
public class Peer {
    Peer()
    {
        System.out.println("New Peer");
        PeerMainLoop.WaitForPeerConnection();
    }
}

class PeerMainLoop
{
    static int listeningPort = 7896; // Peer Listening Port
    
    static void WaitForPeerConnection()
    {
        try {
            ServerSocket listenSocket = new ServerSocket(listeningPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Peer2PeerConnection NewP2PConnection = new Peer2PeerConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class Peer2PeerConnection extends Thread
{
    Peer2PeerConnection(Socket socket)
    {
        
    }
    
    public void run()
    {
        
    }
}