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
        /* 
         * Creación del Socket
         */
        int port = 1989;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Server opened on port: " + port);

        /*
         * En espera de conexiones
         */
        while (true) {
            /*
             * Aceptar conexión del cliente.
             */
            Socket clientSocket = serverSocket.accept();
            System.err.println("New client connected.");
            ServerConnection cNewConnection = new ServerConnection(clientSocket);
        }
    }
}

class ServerConnection extends Thread {

    DataInputStream htmlFile;
    Socket clientSocket;
    HttpRequestHandler cHttpRequestHdlr;

    public ServerConnection(Socket aClientSocket) throws IOException {
        System.out.println("Thread for new client.");
        cHttpRequestHdlr = new HttpRequestHandler(aClientSocket);
        this.start();
    }

    public void run() {

    }
}

class HttpRequestHandler {

    DataInputStream in;
    DataOutputStream out;
    Hashtable<String, String> cHeader = new Hashtable<String, String>();
    Hashtable<String, String> cBodyVar = new Hashtable<String, String>();
    String sReqType;
    String sReqVersion;
    String sUrl;
    String sBoundary;
    
    HttpRequestHandler(Socket clientSocket) {
        try {
            /* Opening input and output streams of data. */
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            /* Get request type, url and version. */
            String s;
            s = in.readLine();
            System.out.println(s);
            String[] sReq = s.split(" ");
            sReqType = sReq[0];
            sUrl = sReq[1];
            sReqVersion = sReq[2];

            /* Create Map with request parameters. */
            while ((s = in.readLine()) != null) {
                System.out.println(s);
                if (!s.isEmpty()) {
                    cHeader.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 2, s.length()));
                } else {
                    break;
                }
            }

            /* Handle POST Request */
            if (sReqType.equals("POST")) {
                String sContentType;
                byte[] bFileInBytes = null;
                int iLength = Integer.parseInt(cHeader.get("Content-Length"));

                System.out.println("Body Length: " + iLength);
                sContentType = cHeader.get("Content-Type");
                if (sContentType.contains("multipart/form-data")) {
                    sBoundary = sContentType.substring(sContentType.indexOf("boundary=") + "boundary=".length(), sContentType.length());
                    System.out.println("Boundary: " + sBoundary);
                    String sVarName;
                    String sFileName;
                    boolean boExit = false;
                    int iFileSize = 0;
                    int pointertoend = 0;

                    while (in.available() > 0 && boExit == false) {
                        s = in.readLine();
                        if (s.contains(sBoundary)) {
                            s = in.readLine();
                            if (s.contains("filename=")) {
                                sFileName = s.substring(s.indexOf("filename=") + "filename=".length() + 1, s.length() - 1);
                                s = s.substring(0, s.indexOf("filename=") - 1);
                                System.out.println("FileName: " + sFileName);
                                sVarName = s.substring(s.indexOf("name=") + "name=".length() + 1, s.length() - 2);
                                System.out.println("VarName: " + sVarName);
                                cBodyVar.put(sVarName, sFileName);
                                /* Content-Type: application/octet-stream */
                                in.readLine();
                                /* \n\r */
                                in.readLine();
                                pointertoend = 0;
                                bFileInBytes = new byte[iLength];
                                while ((iFileSize = in.available()) > 0) {
                                    in.read(bFileInBytes, pointertoend, iFileSize);
                                    pointertoend = pointertoend + iFileSize;
                                    try {
                                        Thread.sleep(20);
                                    } catch (Exception ex) {
                                        System.err.println("Exception: " + ex.getMessage());
                                    }
                                }
                                System.out.println("Finish reading file");
                                boExit = true;
                            } else if (s.contains("name=")) {
                                sVarName = s.substring(s.indexOf("name=") + "name=".length() + 1, s.length() - 1);
                                System.out.println("VarName: " + sVarName);
                                /* \n\r */
                                in.readLine();
                                s = in.readLine();
                                System.out.println("Value: " + s);
                                cBodyVar.put(sVarName, s);
                            }
                        }
                    }

                    /* Output stream */
                    if (bFileInBytes != null) {
                        String sBody;
                        StringBuilder sbBody = new StringBuilder();
                        boolean boCheckIfExist = boCheckIfFileExist(cBodyVar.get("thefile"));
                        if(!boCheckIfExist)
                        {
                        FileOutputStream fileoutputstream = new FileOutputStream(cBodyVar.get("thefile"));
                        BufferedOutputStream bufferedoutput = new BufferedOutputStream(fileoutputstream);
                        bufferedoutput.write(bFileInBytes, 0, pointertoend - sBoundary.length() - 8);
                        bufferedoutput.flush();
                        bufferedoutput.close();
                        fileoutputstream.close();
                        }
                        
                        sbBody.append("<html>");
                        sbBody.append("<head><title>Form</title></head>");
                        sbBody.append("<body>");
                        sbBody.append("Hello ");
                        sbBody.append(cBodyVar.get("firstname"));
                        sbBody.append(" ");
                        sbBody.append(cBodyVar.get("lastname"));
                        sbBody.append(", ");
                        sbBody.append("your file ");
                        sbBody.append(cBodyVar.get("thefile"));
                        if(!boCheckIfExist)
                        {
                            sbBody.append(" was uploaded succesfully.");
                        }
                        else
                        {
                            sbBody.append(" has the same name as another in the server, please try renaming your file.");
                        }
                        sbBody.append("</body>");
                        sbBody.append("</html>");
                        
                        sBody = sbBody.toString();
                        
                        out.write("HTTP/1.1 200 OK\r\n".getBytes());
                        out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n".getBytes());
                        out.write("Server: Apache/0.8.4\r\n".getBytes());
                        out.write("Content-Type: text/html\r\n".getBytes());
                        out.write(("Content-Length: " + sBody.length() + "\r\n").getBytes());
                        out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n".getBytes());
                        out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n".getBytes());
                        out.write("\r\n".getBytes());
                        out.write(sBody.getBytes());
                    }
                    else
                    {
                        vSendResponse(200, "src/error.html");
                    }
                }
            }

            if (sReqType.equals("GET")) {
                if ("/".equals(sUrl)) {
                    vSendResponse(200, "src/index.html");
                } else {
                    boolean boExist = boCheckIfFileExist("src" + sUrl);
                    if (boExist) {
                        vSendResponse(200,"src" + sUrl);
                    } else {
                        vSendResponse(404, "src/404error.html");
                    }
                }

            }

            // on ferme les flux.
            System.err.println("Connexion avec le client terminée");
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.err.println("Exception");
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean boCheckIfFileExist(String sFileName) {
        File varTmpDir;
        boolean boExist;

        varTmpDir = new File(sFileName);
        boExist = varTmpDir.exists();
        return boExist;
    }

    private void vSendResponse(int i32Response, String sFileName) {
        System.out.println("Requested file " + sFileName);
        try {
            File fFile = new File(sFileName);
            String sFile = FileUtils.readFileToString(fFile, "UTF-8");
            if(i32Response == 200)
            {
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            }else if(i32Response == 404)
            {
                out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            }
            out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n".getBytes());
            out.write("Server: Apache/0.8.4\r\n".getBytes());
            out.write("Content-Type: text/html\r\n".getBytes());
            out.write(("Content-Length: " + sFile.length() + "\r\n").getBytes());
            out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n".getBytes());
            out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n".getBytes());
            out.write("\r\n".getBytes());
            out.write(sFile.getBytes());
        } catch (IOException ex) {
            System.err.println("Exception");
            Logger.getLogger(HttpRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
