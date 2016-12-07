/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author uidp1416
 */
public class P2P {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JsonObject personObject = Json.createObjectBuilder()
                .add("name", "John")
                .add("age", 13)
                .add("isMarried", false)
                .add("address", 
                     Json.createObjectBuilder().add("street", "Main Street")
                                               .add("city", "New York")
                                               .add("zipCode", "11111")
                                               .build()
                    )
                .add("phoneNumber", 
                     Json.createArrayBuilder().add("00-000-0000")
                                              .add("11-111-1111")
                                              .add("11-111-1112")
                                              .build()
                    )
                .build();

        System.out.println("Object: " + personObject); 
        Peer NewPeer = new Peer();
    }
    
}
