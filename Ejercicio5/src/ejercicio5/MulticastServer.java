/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejercicio5;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AlumnosA14
 */
public class MulticastServer {

    public static List<RemoteGroup> Remotes = new ArrayList<RemoteGroup>();

    public void run() {
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
            System.out.println("New client");
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String data = in.readUTF();
                boolean WasFound = false;
                if (data.equals("joingroup")) {
                    System.out.println("Join Group Command Received");
                    data = in.readUTF();
                    System.out.println("Join Group Name Command Received " + data);
                    for (RemoteGroup temp : MulticastServer.Remotes) {
                        if (temp.name.equals(data)) {
                            temp.JoinGroup(clientSocket);
                            WasFound = true;
                            break;
                        }
                    }

                    if (!WasFound) {
                        MulticastServer.Remotes.add(new RemoteGroup(data));
                        System.out.println("Added Group " + data);
                        for (RemoteGroup temp : MulticastServer.Remotes) {
                            if (temp.name.equals(data)) {
                                temp.JoinGroup(clientSocket);
                                WasFound = true;
                                break;
                            }
                        }
                    }
                } else if (data.equals("sendmsg")) {
                    WasFound = false;
                    System.out.println("Send Message Command Received");
                    data = in.readUTF();
                    System.out.println("Send Message to Group " + data);
                    for (RemoteGroup temp : MulticastServer.Remotes) {
                        System.out.println("Listing Groups " + temp.name);
                        if (temp.name.equals(data)) {
                            System.out.println("Group " + data + " found.");
                            WasFound = true;
                            data = in.readUTF();
                            System.out.println("Message to send " + data);
                            for (InfoClient tempclient : temp.Clients) {
                                System.out.println("Sending message to client: " + tempclient.clientLocalAddress);
                                out = new DataOutputStream(tempclient.clientSocket.getOutputStream());
                                out.writeUTF(data);
                            }
                            break;
                        }
                    }
                    
                    if(!WasFound)
                    {
                        System.out.println("Group " + data + " was not found");
                    }
                }else if(data.equals("leavegroup")) {
                    System.out.println("Leave Group Command Received");
                    data = in.readUTF();
                    System.out.println("Leaving Group Name Command Received " + data);
                    for (RemoteGroup temp : MulticastServer.Remotes) {
                        if (temp.name.equals(data)) {
                            temp.LeaveGroup(clientSocket);
                            WasFound = true;
                            break;
                        }
                    }

                    if (!WasFound) {
                        System.out.println("Leave Group was not found " + data);
                    }
                }
                else
                {
                    System.out.println("Unknown Command");
                }
                WasFound = false;
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }
    }
}
