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
public class InfoClient
{
    public String inet_address;
    public String group;
    public Socket clientSocket;
    public int clientLocalAddress;
    
    InfoClient()
    {
        inet_address = null;
        group = null;
        clientSocket = null;
    }
    
    InfoClient(Socket clientSocket)
    {
        this.inet_address = clientSocket.getInetAddress().toString();
        this.clientSocket = clientSocket;
        this.clientLocalAddress = clientSocket.getPort();
    }
}
