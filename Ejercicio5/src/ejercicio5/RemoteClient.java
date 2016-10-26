/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejercicio5;

import java.io.*;
import java.net.*;

/**
 *
 * @author AlumnosA14
 */
public class RemoteClient
{
    Socket s = null;
    DataOutputStream out;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public void run(String ServerAddress)
    {
        try {
            int serverPort = 7896;
            s = new Socket(ServerAddress, serverPort);
            ClientConnectionRead ReadCon = new ClientConnectionRead(s);
            String UserSelection;
            while(true)
            {
                System.out.println("Select and action: ");
                System.out.println("1. Join Group ");
                System.out.println("2. Leave Group ");
                System.out.println("3. Send Message to Group ");
                UserSelection = br.readLine();
                if("1".equals(UserSelection))
                {
                    System.out.println("Group?");
                    UserSelection = br.readLine();
                    boJoin(UserSelection);
                }else if("2".equals(UserSelection))
                {
                    System.out.println("Group?");
                    UserSelection = br.readLine();
                    boLeave(UserSelection);
                } else if("3".equals(UserSelection))
                {
                    System.out.println("Group?");
                    UserSelection = br.readLine();
                    boSend(UserSelection);
                }
                else
                {
                    System.out.println("Invalid option selected.");
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }
    
    private boolean boJoin(String group) throws IOException
    {
        out = new DataOutputStream(s.getOutputStream());
        out.writeUTF("joingroup");
        out.writeUTF(group);
        return true;
    }
    
    private boolean boLeave(String group) throws IOException
    {
        out = new DataOutputStream(s.getOutputStream());
        out.writeUTF("leavegroup");
        out.writeUTF(group);
        return true;
    }
    
    private boolean boSend(String group) throws IOException
    {
        out.writeUTF("sendmsg");
        out = new DataOutputStream(s.getOutputStream());
        out.writeUTF(group);
        System.out.println("Message?");
        out.writeUTF(br.readLine());
        return true;
    }
}

class ClientConnectionRead extends Thread {

    DataInputStream in;
    Socket clientSocket;

    public ClientConnectionRead(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            while(true)
            {
                String data = in.readUTF();
                System.out.println("Mensaje recibido: " + data);
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline in thread:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }
    }
}
