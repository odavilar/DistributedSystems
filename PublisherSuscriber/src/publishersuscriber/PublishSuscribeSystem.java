/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author uidp1416
 */
public class PublishSuscribeSystem {

    public static Hashtable TopicsTable = new Hashtable();
    
    PublishSuscribeSystem() {
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                ServerConnection c = new ServerConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class ServerConnection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public ServerConnection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("New client " + clientSocket.getPort() );
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        String s = null;
        try {
            out.writeUTF("Welcome to Publish Suscribe System");
            while (true) {
                System.out.println("[DEBUG] Waiting Message...");
                s = in.readUTF();
                System.out.println("[DEBUG] Message received: " + s);
                Message cMsg = new Message();
                if(0 != cMsg.i32ParseMessage(s))
                {
                    out.writeUTF("Error parsing message.");
                    System.out.println("[DEBUG] Error parsing message.");
                }
                else
                {
                    out.writeUTF(s);
                    System.out.println("[DEBUG] Message sent.");
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class Topic
{
    private List<SuscriberInfo> SuscriberList = null;
    private String sTopicName = null;
    
    Topic(String sName)
    {
        sTopicName = sName;
        SuscriberList = new ArrayList<>();
    }
    
    String sGetTopicName()
    {
        return sTopicName;
    }
    
    void vAddSuscriber(SuscriberInfo cNewSus)
    {
        SuscriberList.add(cNewSus);
    }
    
    void vDelSuscriber(String sUserName)
    {
        for (int i = 0; i < SuscriberList.size(); i++) {
            if (SuscriberList.get(i).sGetUserName().equals(sUserName)) {
                SuscriberList.remove(i);
            }
        }
    }
}

class SuscriberInfo
{
    String sUserName = null;
    
    SuscriberInfo(String sName)
    {
        this.sUserName = sName;
    }
    
    String sGetUserName()
    {
        return sUserName;
    }
}

class Message
{
    List<String> TopicList = null;
    String sMessage = null;
    String sMsg = null;
    
    Message()
    {
        TopicList = new ArrayList<>();
        sMessage = new String();
    }
    
    public void vAddTopic(String sTopicName)
    {
        
    }
    
    public int i32ParseMessage(String sMsg)
    {
        int i32Error = 0;
        Pattern p = Pattern.compile("(#.+)\\s:\\s(.*)");
        Matcher m = p.matcher(sMsg);
        if (m.matches()) {
            String topics = m.group(1);
            System.out.println(topics);
            sMessage = m.group(2);
            System.out.println(sMessage);
            String topicsList[] = topics.split(" ");
            for(int i = 0; i < topicsList.length; i++)
            {
                topicsList[i] = topicsList[i].substring(1);
                System.out.println(topicsList[i]);
                TopicList.add(topicsList[i]);
            }
        }
        else
        {
            System.out.println("Does not match format");
            i32Error = -1;
        }
        
        return i32Error;
    }
}