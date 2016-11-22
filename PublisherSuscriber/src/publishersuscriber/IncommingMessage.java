/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishersuscriber;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author uidp1416
 */
class IncommingMessage extends Thread {

    DataInputStream in;
    Socket clientSocket;

    public IncommingMessage(Socket aClientSocket) {
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
                System.out.println("Received Message: " + data);
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Readline in thread:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) { /*close failed*/
            }
        }
    }
}