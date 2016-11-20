/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uidp1416
 */
public class Publisher {
    Socket s = null;
    
    Publisher()
    {
        try {
            int serverPort = 7896;
            s = new Socket("127.0.0.1", serverPort);
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Publisher(String sAddress)
    {
        try {
            int serverPort = 7896;
            s = new Socket(sAddress, serverPort);
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void vPublish()
    {
        
    }
}
