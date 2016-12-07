/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uidp1416
 */
public class Peer {

    Peer() {
        System.out.println("New Peer");
        PeerWait4PeerConnection PW4PConnection = new PeerWait4PeerConnection();
    }
}

class PeerWait4PeerConnection extends Thread {

    static int listeningPort = 7896; // Peer Listening Port

    PeerWait4PeerConnection()
    {
        this.run();
    }
    
    public void run()
    {
        WaitForPeerConnection();
    }
    
    static void WaitForPeerConnection() {
        try {
            ServerSocket listenSocket = new ServerSocket(listeningPort);
            System.out.println("Waiting for connection at: " + listeningPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Peer2PeerConnectionHandler P2PCHdl = new Peer2PeerConnectionHandler(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }

    
}

class Peer2PeerConnectionHandler extends Thread {

    Peer2PeerConnectionHandler(Socket socket) {
        System.out.println("Connection accepted from " + socket.getRemoteSocketAddress().toString() + " at port " + socket.getPort());
        this.run();
    }

    public void run() {

    }
    
    /*
        Sends a topic to a peer. dt
    */
    static void deliverTopic() {

    }
}

class Peer2PeerConnectionRequest extends Thread {

    Peer2PeerConnectionRequest(Socket socket) {

    }

    public void run() {
        
    }
    
    /*
        Request a topic from a peer. rt
     */
    static void requestTopic() {

    }
}

class Peer2ServerConnection extends Thread {

    Peer2ServerConnection() {
        try {
            System.out.println("1. Suscribe to topic");
            System.out.println("2. Unsuscribe to topic");
            System.out.println("3. Publish topic");
            System.out.println("4. Unpublish topic");
            System.out.println("5. Get topic");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            if ("1".equals(s))
            {
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {

    }

    /*
      Request peers from know topic to the server.  rp topic
      String
     */
    static void requestPeers() {

    }

    /*
        Publish topic to a server. pt topic : msg
        return: OK GUID | NOK
     */
    static void publishTopic() {

    }

    /*
        ut topic
        return: OK | NOK
     */
    static void unpublishTopic() {

    }

    /*
        Suscribe peer to topic. st topic
        return: OK | NOK
     */
    static void suscribeTopic() {

    }

    /*
        Unsuscribe peer from topic. un topic
        return: OK | NOK
     */
    static void unsuscribeTopic() {

    }
    
    /*
        Update server index.
        iu del topic
        iu add topic
        return: OK | NOK
     */
    static void updateIndex() {

    }
}
