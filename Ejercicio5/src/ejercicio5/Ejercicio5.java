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
public class Ejercicio5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("1. Act as Server\n2. Act as Client");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        if ("1".equals(s)) {
            System.out.println("Server running");
            MulticastServer mcServer = new MulticastServer();
            mcServer.run();
        }
        if ("2".equals(s)) {
            RemoteClient remoteClient = new RemoteClient();
            String serverAddress;
            System.out.println("Client running");
            System.out.print("Please enter a server ip: ");
            serverAddress = br.readLine();
            remoteClient.run(serverAddress);
        }
    }    
}