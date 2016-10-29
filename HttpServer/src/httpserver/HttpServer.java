/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.net.*;
import java.io.*;
import java.util.Hashtable;
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

    DataInputStream htmlFile;
    BufferedInputStream BufferedInput;
    Socket clientSocket;
    HttpRequestHandler cHttpRequestHdlr;

    public ServerConnection(Socket aClientSocket) throws IOException {
        System.out.println("New client");
        cHttpRequestHdlr = new HttpRequestHandler(aClientSocket);
        this.start();
    }

    public void run() {

    }
}

class HttpRequestHandler {

    BufferedReader in;
    BufferedWriter out;
    Hashtable<String, String> cHeader = new Hashtable<String, String>();
    String sReqType;
    String sReqVersion;
    String sUrl;

    HttpRequestHandler(Socket clientSocket) throws IOException {
        try {
            // on ouvre un flux de converation
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Get request type, url and version. */
        String s;
        s = in.readLine();
        System.out.println(s);
        String[] sReq = s.split(" ");
        sReqType = sReq[0];
        sUrl = sReq[1];
        sReqVersion = sReq[2];
        while ((s = in.readLine()) != null) {
            System.out.println(s);
            if (!s.isEmpty()) {
                cHeader.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 2, s.length()));
            } else {
                break;
            }
        }

        if (sReqType.equals("POST")) {
            int i = 0;
            int ctr = 0;
            char c;
            int iLength = Integer.parseInt(cHeader.get("Content-Length"));
            System.out.println("Body Length: " + iLength);
            while ((i = in.read()) != -1) {
                // int to character
                c = (char) i;
                // print char
                System.out.println("Character Read: " + c + " " + ++ctr);
                if(ctr == iLength)
                {
                    break;
                }
            }
        }

        File fFile = new File("src/index.html");
        String sFile = FileUtils.readFileToString(fFile, "UTF-8");

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
    }

}
