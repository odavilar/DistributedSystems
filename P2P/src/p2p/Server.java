package p2p;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.json.Json;
import javax.json.JsonObject;


public class Server
{
    public  static Hashtable   gTopics        = null;
    private List<ServerSocket> gServer        = null;
    public  static final int   PEER_PORT      = 9876;
    private static int         gGUIDGenerator = 0;

    Server()
    {
        gTopics  = new Hashtable();
        gServer  = new ArrayList<>();
        PeerListener cPeerThread = new PeerListener();
    }
    
    public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException
    {
        Server P2PServer = new Server();
        System.out.println("P2P System - RUNNING." + InetAddress.getLocalHost().getHostAddress() + ":" + Server.PEER_PORT);
    }
    
    public static int generateGUID()
    {
        return ++gGUIDGenerator;
    }
    
}

class PeerListener extends Thread
{
    private boolean bRun = true;
    
    PeerListener()
    {
        this.start();
    }

    @Override
    public void run()
    {
        try
        {
            ServerSocket listenSocket = new ServerSocket(Server.PEER_PORT);
            System.out.println("[PeerListener] Publish socket: " + InetAddress.getLocalHost().getHostAddress());
           
            while (this.bRun)
            {
                Socket clientSocket  = listenSocket.accept();
                PeerConnection oPeer = new PeerConnection(clientSocket);
            }
        }
        catch (IOException e)
        {
            System.out.println("[PeerListener] Publish socket: " + e.getMessage());
        }
    }
    
    public void stopListener()
    {
        this.bRun = false;
    }
}

class PeerConnection extends Thread
{
    DataInputStream  in;
    DataOutputStream out;
    Socket clientSocket;

    public PeerConnection(Socket aClientSocket)
    {
        try
        {
            clientSocket = aClientSocket;
            in           = new DataInputStream(clientSocket.getInputStream());
            out          = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("[PeerConnection] New Peer " + clientSocket.getPort());
            this.start();
        }
        catch (IOException e)
        {
            System.out.println("[PeerConnection] Connection:" + e.getMessage());
        }
    }

    public void run()
    {
        String sCMD;
        
        try
        {
            out.writeUTF("[OK] P2P : Connection Successful");
            while (true)
            {
                System.out.println("[PeerConnection] Waiting for CMD...");
                sCMD = in.readUTF();
                System.out.println("[PeerConnection] Message received: " + sCMD);
                PeerParser cEvent = new PeerParser();
                if (0 != cEvent.parse(sCMD))
                {
                    out.writeUTF("NOK");
                    System.out.println("[PeerConnection] Error parsing message.");
                }
                else
                {
                    Topic cTopic;
                    
                    switch(cEvent.CMD)
                    {
                        /* Index Update - del topic ; add topic */
                        case "iu":
                            if (Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                cTopic = (Topic)Server.gTopics.get(cEvent.TOPIC);
                                switch (cEvent.DO)
                                {
                                    case "add":
                                        cTopic.addPeer(clientSocket);
                                        out.writeUTF("OK");
                                        break;
                                    case "del":
                                        cTopic.deletePeer(clientSocket);
                                        out.writeUTF("OK");
                                        break;
                                    default:
                                        out.writeUTF("NOK");
                                        break;
                                }
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        /* Publish Topic - topic*/
                        case "pt":
                            if (!Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                cTopic = new Topic(cEvent.TOPIC);
                                cTopic.addPeer(clientSocket);
                                Server.gTopics.put(cEvent.TOPIC, cTopic);
                                System.out.println("[PeerConnection] New topic " + cTopic.getGUID());
                                out.writeUTF("OK " + Integer.toString(cTopic.getGUID()));
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        /* Unpublish Topic - topic */
                        case "ut":
                            if (Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                Server.gTopics.remove(cEvent.TOPIC);
                                out.writeUTF("OK");
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        /* Subscribe Topic - topic */
                        case "st":
                            if (Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                cTopic = (Topic)Server.gTopics.get(cEvent.TOPIC);
                                cTopic.addSuscriber(clientSocket);
                                out.writeUTF("OK");
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        /* Request Peer List - topic */
                        case "rp":
                            if (Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                cTopic = (Topic)Server.gTopics.get(cEvent.TOPIC);
                                StringBuilder jSONTopic = new StringBuilder(
                                "{" + "\"" + cTopic.getName() + "\" : { \"subscribers\" : [  ");
                                for (Socket it : cTopic.getSusbcriberList())
                                {
                                    jSONTopic.append("{ \"id\" : \"").append(it.getPort()).append("\",");
                                    jSONTopic.append(  "\"ip\" : \"").append(it.getInetAddress()).append(":").append(it.getLocalPort()).append("\"},");
                                }
                                jSONTopic.setLength(jSONTopic.length() - 1);
                                jSONTopic.append("], \"peers\" : [  ");
                                for (Socket it : cTopic.getPeerList())
                                {
                                    jSONTopic.append("{ \"id\" : \"").append(it.getPort()).append("\",");
                                    jSONTopic.append(  "\"ip\" : \"").append(it.getInetAddress()).append(":").append(it.getLocalPort()).append("\"},");
                                }
                                jSONTopic.setLength(jSONTopic.length() - 1);
                                jSONTopic.append("] }");
                                out.writeUTF("OK " + jSONTopic);
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        /* Unsubscribe Topic - topic */
                        case "un":
                            if (Server.gTopics.containsKey(cEvent.TOPIC))
                            {
                                cTopic = (Topic)Server.gTopics.get(cEvent.TOPIC);
                                cTopic.deleteSuscriber(clientSocket);
                                out.writeUTF("OK");
                            }
                            else
                            {
                                out.writeUTF("NOK");
                            }
                            break;
                        default:
                            out.writeUTF("NOK");
                            break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("[PeerConnection] Error : " + e.getMessage());
        }
    }
}

class Topic
{
    private int          gGUID          = 0;
    private String       gName          = null;
    private List<Socket> SubscriberList = null;
    private List<Socket> PeersList      = null;


    Topic(String sName)
    {
        SubscriberList = new ArrayList<>();
        PeersList      = new ArrayList<>();
        gGUID          = Server.generateGUID();
        gName          = sName;
    }
    
    String getName()
    {
        return gName;
    }
        
    int getGUID()
    {
        return gGUID;
    }

    void addPeer(Socket cNewPeer)
    {
        PeersList.add(cNewPeer);
    }
            
    void addSuscriber(Socket cNewSubs)
    {
        SubscriberList.add(cNewSubs);
    }

    void deleteSuscriber(Socket cDelSubs)
    {
        for (int i = 0; i < SubscriberList.size(); i++)
        {
            if (SubscriberList.get(i).getPort() == cDelSubs.getPort())
            {
                SubscriberList.remove(i);
            }
        }
    }

    void deletePeer(Socket cDelPeer)
    {
        for (int i = 0; i < PeersList.size(); i++)
        {
            if (PeersList.get(i).getPort() == cDelPeer.getPort())
            {
                PeersList.remove(i);
            }
        }
    }
        
    List<Socket> getSusbcriberList()
    {
        return SubscriberList;
    }
    
    List<Socket> getPeerList()
    {
        return PeersList;
    }
    
    boolean suscriberExists(Socket cSocket)
    {
        boolean boRet = false;
        for (Socket it : SubscriberList)
        {
            if (it.getPort() == cSocket.getPort())
            {
                boRet = true;
            }
        }
        return boRet;
    }
}

class PeerParser
{
    public String CMD   = null;
    public String DO    = null;
    public String TOPIC = null;

    PeerParser()
    {
    }
    
    public int parse(String sEvent)
    {
        int iError = -1;
        
        if (sEvent != null)
        {
            StringTokenizer sToken = new StringTokenizer(sEvent);
            this.CMD               = sToken.nextToken();
            iError                 = 0;
            switch (sToken.countTokens())
            {
                case 1:
                    this.TOPIC = sToken.nextToken();
                    break;
                case 2:
                    this.DO    = sToken.nextToken();
                    this.TOPIC = sToken.nextToken();
                    break;
                default:
                    iError = -1;
                    break;
            }
        }

        return iError;
    }
}
