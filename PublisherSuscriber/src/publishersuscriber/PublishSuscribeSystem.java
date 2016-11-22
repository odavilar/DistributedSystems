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
        PublisherThread cPubThread = new PublisherThread();
        SuscriberThread cSusThread = new SuscriberThread();
    }
}

class PublisherThread extends Thread {

    PublisherThread() {
        this.start();
    }

    public void run() {
        try {
            int serverPort = 7896; // the publisher port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                PublisherConnection c = new PublisherConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class SuscriberThread extends Thread {

    SuscriberThread() {
        this.start();
    }

    public void run() {
        try {
            int serverPort = 7899; // the publisher port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                SuscriberConnection c = new SuscriberConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class PublisherConnection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public PublisherConnection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("New publisher " + clientSocket.getPort());
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
                System.out.println("[DEBUG PublisherConnection] Waiting Message...");
                s = in.readUTF();
                System.out.println("[DEBUG PublisherConnection] Message received: " + s);
                PublisherMessage cMsg = new PublisherMessage();
                if (0 != cMsg.i32ParseMessage(s)) {
                    out.writeUTF("Error parsing message.");
                    System.out.println("[DEBUG PublisherConnection] Error parsing message.");
                } else {
                    for (String temp : cMsg.TopicList) {
                        if (!PublishSuscribeSystem.TopicsTable.containsKey(temp)) {
                            Topic cNewTopic = new Topic(temp);
                            cNewTopic.vAddMsg(cMsg.sMessage);
                            PublishSuscribeSystem.TopicsTable.put(temp, cNewTopic);
                            System.out.println("[DEBUG PublisherConnection] New topic " + cNewTopic.sGetTopicName() + " added with message " + cMsg.sMessage);
                        } else {
                            Topic cTopic = (Topic) PublishSuscribeSystem.TopicsTable.get(temp);
                            cTopic.vAddMsg(cMsg.sMessage);
                            System.out.println("[DEBUG PublisherConnection] Message added to topic " + cTopic.sGetTopicName());
                        }
                    }

                }

            }
        } catch (IOException ex) {
            if (ex.getMessage().equals("Connection reset")) {
                System.out.println("Publisher disconnected: " + clientSocket.getPort());
            } else {
                Logger.getLogger(PublisherConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class SuscriberConnection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public SuscriberConnection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("New suscriber " + clientSocket.getPort());
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
                System.out.println("[DEBUG SuscriberConnection] Waiting Message...");
                s = in.readUTF();
                System.out.println("[DEBUG SuscriberConnection] Message received: " + s);
                SuscriberMessage cMsg = new SuscriberMessage();
                if (0 != cMsg.i32ParseMessage(s)) {
                    out.writeUTF("Error parsing message.");
                    System.out.println("[DEBUG SuscriberConnection] Error parsing message.");
                } else if (cMsg.sCommand.equals("suscribe")) {
                    for (String temp : cMsg.TopicList) {
                        if (!PublishSuscribeSystem.TopicsTable.containsKey(temp)) {
                            Topic cNewTopic = new Topic(temp);
                            PublishSuscribeSystem.TopicsTable.put(temp, cNewTopic);
                            System.out.println("[DEBUG SuscriberConnection] New topic " + cNewTopic.sGetTopicName() + " added.");
                        }
                        
                        Topic cTopic = (Topic) PublishSuscribeSystem.TopicsTable.get(temp);
                        if (!cTopic.boCheckIfSuscriberExist(clientSocket)) {
                            SuscriberInfo cNewSus = new SuscriberInfo(clientSocket);
                            cTopic.vAddSuscriber(cNewSus);
                        }
                        else
                        {
                            out.writeUTF("You are already suscribed to topic " + temp);
                        }
                        
                    }
                } else if (cMsg.sCommand.equals("unsuscribe")) {
                    for (String temp : cMsg.TopicList) {
                        if (!PublishSuscribeSystem.TopicsTable.containsKey(temp)) {
                            Topic cNewTopic = new Topic(temp);
                            PublishSuscribeSystem.TopicsTable.put(temp, cNewTopic);
                            System.out.println("[DEBUG SuscriberConnection] New topic " + cNewTopic.sGetTopicName() + " added.");
                        }
                        
                        Topic cTopic = (Topic) PublishSuscribeSystem.TopicsTable.get(temp);
                        if (!cTopic.boCheckIfSuscriberExist(clientSocket)) {
                            cTopic.vDelSuscriber(clientSocket);
                        }
                        else
                        {
                            out.writeUTF("You are not suscribed to topic " + temp);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SuscriberConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class Topic {

    private List<SuscriberInfo> SuscriberList = null;
    private List<String> cMessage = null;
    private String sTopicName = null;

    Topic(String sName) {
        sTopicName = sName;
        SuscriberList = new ArrayList<>();
        cMessage = new ArrayList<>();
    }

    String sGetTopicName() {
        return sTopicName;
    }

    void vAddSuscriber(SuscriberInfo cNewSus) {
        SuscriberList.add(cNewSus);
    }

    void vDelSuscriber(Socket cSocket) {
        for (int i = 0; i < SuscriberList.size(); i++) {
            if (SuscriberList.get(i).sGetUserSocket().getPort() == cSocket.getPort()) {
                SuscriberList.remove(i);
            }
        }
    }

    void vAddMsg(String cMsg) {
        cMessage.add(cMsg);
    }

    boolean boCheckIfSuscriberExist(Socket cSocket) {
        boolean boRet = false;
        for (SuscriberInfo temp : SuscriberList) {
            if (temp.sGetUserSocket().getPort() == cSocket.getPort()) {
                boRet = true;
            }
        }
        return boRet;
    }
}

class SuscriberInfo {

    Socket cSocket = null;

    SuscriberInfo(Socket cSocket) {
        this.cSocket = cSocket;
    }

    Socket sGetUserSocket() {
        return cSocket;
    }
}

class PublisherMessage {

    public List<String> TopicList = null;
    public String sMessage = null;

    PublisherMessage() {
        TopicList = new ArrayList<>();
        sMessage = new String();
    }

    public int i32ParseMessage(String sMsg) {
        int i32Error = 0;
        Pattern p = Pattern.compile("(#.+)\\s:\\s(.*)");
        Matcher m = p.matcher(sMsg);
        if (m.matches()) {
            String topics = m.group(1);
            System.out.println(topics);
            sMessage = m.group(2);
            System.out.println(sMessage);
            String topicsList[] = topics.split(" ");
            for (int i = 0; i < topicsList.length; i++) {
                topicsList[i] = topicsList[i].substring(1);
                System.out.println(topicsList[i]);
                TopicList.add(topicsList[i]);
            }
        } else {
            System.out.println("Does not match format");
            i32Error = -1;
        }

        return i32Error;
    }
}

class SuscriberMessage {

    public List<String> TopicList = null;
    public String sCommand = null;

    SuscriberMessage() {
        TopicList = new ArrayList<>();
        sCommand = new String();
    }

    public int i32ParseMessage(String sMsg) {
        int i32Error = 0;
        Pattern p = Pattern.compile("(\\bsuscribe|\\bunsuscribe)\\s(#.+)");
        Matcher m = p.matcher(sMsg);
        if (m.matches()) {
            sCommand = m.group(1);
            String topics = m.group(2);
            System.out.println(sCommand);
            String topicsList[] = topics.split(" ");
            for (int i = 0; i < topicsList.length; i++) {
                topicsList[i] = topicsList[i].substring(1);
                System.out.println(topicsList[i]);
                TopicList.add(topicsList[i]);
            }
        } else {
            System.out.println("Does not match format");
            i32Error = -1;
        }

        return i32Error;
    }
}
