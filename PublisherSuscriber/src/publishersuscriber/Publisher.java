/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uidp1416
 */
public class Publisher {
    Socket s = null;
    DataOutputStream out = null;
    
    Publisher()
    {
        try {
            int serverPort = 7896;
            s = new Socket("127.0.0.1", serverPort);
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Publisher(String sAddress)
    {
        try {
            int serverPort = 7896;
            s = new Socket(sAddress, serverPort);
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void vRun()
    {
        IncommingMessage cIM = new IncommingMessage(s);
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String sLine = null;
            System.out.println("Message format: #topic #topic #topic : Message.");
            while(true)
            {        
                sLine = br.readLine();
                out.writeUTF(sLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}