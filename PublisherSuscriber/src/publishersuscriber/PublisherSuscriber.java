/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uidp1416
 */
public class PublisherSuscriber {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("1. Publish-Suscribe System");
            System.out.println("2. Publisher");
            System.out.println("3. Suscriber");
            System.out.print("Please select an option [3]: ");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            if ("1".equals(s))
            {
                System.out.println("Publish-Suscribe System is being launched.");
                PublishSuscribeSystem cPubSusSys = new PublishSuscribeSystem();
            }
            if ("2".equals(s))
            {
                System.out.println("Publisher is being launched.");
                Publisher cPublisher = new Publisher();
                cPublisher.vRun();
            }
            if ("3".equals(s) || s.isEmpty())
            {
                System.out.println("Suscriber is being launched.");
                Suscriber cSuscriber = new Suscriber();
                cSuscriber.vRun();
            }
        } catch (IOException ex) {
            Logger.getLogger(PublisherSuscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
