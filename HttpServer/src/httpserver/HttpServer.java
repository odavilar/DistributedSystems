/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author uidp1416
 */
public class HttpServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        // création de la socket
        int port = 1989;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Serveur lancé sur le port : " + port);

        // repeatedly wait for connections, and process
        while (true) {
            // on reste bloqué sur l'attente d'une demande client
            Socket clientSocket = serverSocket.accept();
            System.err.println("Nouveau client connecté");
            ServerConnection cNewConnection = new ServerConnection(clientSocket);

        }
    }
}

class ServerConnection extends Thread {

    BufferedReader in;
    BufferedWriter out;
    DataInputStream htmlFile;
    BufferedInputStream BufferedInput;
    String sFileName = new String("index.html");
    Socket clientSocket;

    public ServerConnection(Socket aClientSocket) {
            clientSocket = aClientSocket;
            try {
                // on ouvre un flux de converation
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            } catch (IOException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("New client");
            
            this.start();
    }

    public void run() {

        try {
            // chaque fois qu'une donnée est lue sur le réseau on la renvoi sur
            // le flux d'écriture.
            // la donnée lue est donc retournée exactement au même client.
            String s;
            while ((s = in.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    break;
                }
            }
            File fFile = new File("index.html");
            String sFile = FileUtils.readFileToString(fFile,"UTF-8");
            
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
            out.write("Server: Apache/0.8.4\r\n");
            out.write("Content-Type: text/html\r\n");

            out.write("Content-Length: " + sFile.length() + "\r\n");
            out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
            out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
            out.write("\r\n");
            out.write(sFile);
            // on ferme les flux.
            System.err.println("Connexion avec le client terminée");
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
