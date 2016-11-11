/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejercicio5;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AlumnosA14
 */
public class RemoteGroup {

    public String name;
    public List<InfoClient> Clients = new ArrayList<>();

    RemoteGroup(String name) {
        this.name = name;
    }

    public void JoinGroup(Socket clientSocket) {
        boolean boFound = false;
        for (int i = 0; i < Clients.size(); i++) {
            if (Clients.get(i).clientLocalAddress == clientSocket.getPort()) {
                boFound = true;
            }
        }

        if (!boFound) {
            Clients.add(new InfoClient(clientSocket));
        }
    }

    public void LeaveGroup(Socket clientSocket) {
        for (int i = 0; i < Clients.size(); i++) {
            if (Clients.get(i).clientLocalAddress == clientSocket.getPort()) {
                Clients.remove(i);
            }
        }
    }
}
