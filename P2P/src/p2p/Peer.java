/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author uidp1416
 */
public class Peer {

    private PeerWait4PeerConnection PW4PConnection;
    private Peer2ServerConnection P2SConnection;

    Peer() {
        System.out.println("New Peer");
        PW4PConnection = new PeerWait4PeerConnection();
        P2SConnection = new Peer2ServerConnection();

        System.out.println(Color.Green + "Available commands" + Color.ClearFormat);
        System.out.println("1. Suscribe to topic: sus topic");
        System.out.println("2. Unsuscribe to topic: unsus topic");
        System.out.println("3. Publish topic: pub topic");
        System.out.println("4. Unpublish topic: unpub topic");
        System.out.println("5. Get topic: request topic");
        System.out.print("[me@localhost]$ ");
        this.waitForCommand();
    }

    void waitForCommand() {
        boolean boExit = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!boExit) {
            try {
                String s = br.readLine();
                if (!s.equals("q")) {
                    String sArr[] = s.split(" ");
                    System.out.println("Received command: " + sArr[0] + " topic: " + sArr[1]);
                    switch (sArr[0]) {
                        case "sus":
                            P2SConnection.suscribeTopic(sArr[1]);
                            break;
                        case "unsus":
                            P2SConnection.unsuscribeTopic(sArr[1]);
                            break;
                        case "pub":
                            P2SConnection.publishTopic(sArr[1]);
                            break;
                        case "unpub":
                            P2SConnection.unpublishTopic(sArr[1]);
                            break;
                        case "request":
                            P2SConnection.requestTopic(sArr[1]);
                            break;
                        default:
                            System.out.println("Command not recognized.");
                            break;
                    }
                } else {
                    boExit = true;
                    System.out.println("Exit.");
                }
            } catch (IOException | ArrayIndexOutOfBoundsException ex) {
                System.out.println(Color.Red + "Message is in wrong format." + Color.ClearFormat);
                waitForCommand();
            }
        }
    }
}

class PeerWait4PeerConnection extends Thread {

    static int listeningPort = 7897; // Peer Listening Port

    PeerWait4PeerConnection() {
        this.start();
    }

    public void run() {
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
        this.start();
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

    Socket serverSocket;
    String serverIpAddress = "127.0.0.1";
    int serverPort = 9876;
    DataInputStream in;
    DataOutputStream out;

    JsonArray jaSuscribers;
    JsonArray jaPeers;
    
    Peer2ServerConnection() {
        initConnection();
    }

    Peer2ServerConnection(String IpAddress) {
        serverIpAddress = IpAddress;
        initConnection();
    }

    Peer2ServerConnection(String IpAddress, int Port) {
        serverIpAddress = IpAddress;
        serverPort = Port;
        initConnection();
    }

    void initConnection() {
        try {
            serverSocket = new Socket(serverIpAddress, serverPort);
            in = new DataInputStream(serverSocket.getInputStream());
            out = new DataOutputStream(serverSocket.getOutputStream());
            this.start();
        } catch (IOException ex) {
            System.out.println(Color.Red + "Error: " + ex.getMessage() + " from Server");
        }
    }

    @Override
    public void run() {

        while (true) {
            /* Wait for response */
            try {
                String cmd = in.readUTF();
                String cmdArr[];
                System.out.println(Color.Cyan + cmd);
                try {
                    cmdArr = cmd.split(" ", 3);
                } catch (PatternSyntaxException ex) {
                    cmdArr = new String[1];
                    cmdArr[0] = cmd;
                }
                switch (cmdArr[0]) {
                    case "OK":
                        System.out.println(Color.Blue + "Received OK");
                        if (cmdArr.length > 1) {
                            if (cmdArr[1].equals("pt")) {
                                System.out.println("guid: " + cmdArr[2]);
                            } else if (cmdArr[1].equals("rp")) {
                                JsonReader jsonReader = Json.createReader(new StringReader(cmdArr[2]));
                                JsonObject jsonObject = jsonReader.readObject();
                                for (String key : jsonObject.keySet()) {
                                    JsonObject tempJsonObj = jsonObject.getJsonObject(key);
                                    for (String TopicKey : tempJsonObj.keySet()) {
                                        if(TopicKey.equals("suscribers"))
                                        {
                                            jaSuscribers = tempJsonObj.getJsonArray(TopicKey);
                                        }else if(TopicKey.equals("peers"))
                                        {
                                            jaPeers = tempJsonObj.getJsonArray(TopicKey);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "NOK":
                        System.out.println(Color.Blue + "Received NOK");
                        break;
                    default:
                        System.out.println("Received cmd: " + cmd);
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
      Request peers from know topic to the server.  rp topic
      String
     */
    public void requestPeers(String sTopic) {
        try {
            System.out.println("Request Peers List");
            out.writeUTF("rp " + sTopic);
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        Publish topic to a server. pt topic : msg
        return: OK GUID | NOK
     */
    public void publishTopic(String sTopic) {
        try {
            System.out.println("Publish topic");
            out.writeUTF("pt " + sTopic);
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        ut topic
        return: OK | NOK
     */
    public void unpublishTopic(String sTopic) {
        try {
            System.out.println("Unpublish topic");
            out.writeUTF("ut " + sTopic);
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        Suscribe peer to topic. st topic
        return: OK | NOK
     */
    public void suscribeTopic(String sTopic) {
        try {
            System.out.println("Suscribe to topic");
            out.writeUTF("st " + sTopic);
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        Unsuscribe peer from topic. un topic
        return: OK | NOK
     */
    public void unsuscribeTopic(String sTopic) {
        try {
            System.out.println("Unsuscribe to topic");
            out.writeUTF("un " + sTopic);
        } catch (IOException ex) {
            Logger.getLogger(Peer2ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        Update server index.
        iu del topic
        iu add topic
        return: OK | NOK
     */
    public void updateIndex() {
        System.out.println("Update index");
    }

    public void requestTopic(String sTopic) {
        System.out.println("Request topic");
        this.requestPeers(sTopic);
    }
}

class Color {

    static String Black = (char) 27 + "[30m";
    static String Red = (char) 27 + "[31m";
    static String Green = (char) 27 + "[32m";
    static String Yellow = (char) 27 + "[33m";
    static String Blue = (char) 27 + "[34m";
    static String Magenta = (char) 27 + "[35m";
    static String Cyan = (char) 27 + "[36m";
    static String White = (char) 27 + "[37m";
    static String BlackBg = (char) 27 + "[40m";
    static String RedBg = (char) 27 + "[41m";
    static String GreenBg = (char) 27 + "[42m";
    static String YellowBg = (char) 27 + "[43m";
    static String BlueBg = (char) 27 + "[44m";
    static String MagentaBg = (char) 27 + "[45m";
    static String CyanBg = (char) 27 + "[46m";
    static String WhiteBg = (char) 27 + "[47m";
    static String Bright = (char) 27 + "[1m";
    static String StopBright = (char) 27 + "[21m";
    static String Underline = (char) 27 + "[4m";
    static String StopUnderline = (char) 27 + "[24m";
    static String ClearFormat = (char) 27 + "[0m";
}
