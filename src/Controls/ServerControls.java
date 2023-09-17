/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class ServerControls {
    public ServerSocket sv; 
    public Socket client; 
    public DataOutputStream out;
    public DataInputStream in; 
    public ServerControls() throws IOException{
        startServer(7777);
        //acceptClient();
        
    }
    public void startServer(int port) throws IOException{
        sv = new ServerSocket(port);
    }
    public void acceptClient() throws IOException{
        client = sv.accept();
        out = new DataOutputStream(client.getOutputStream());
        in = new DataInputStream(client.getInputStream());
    }
    public void receiveMessage() throws IOException{
        System.out.println("Client: " + in.readUTF());
    }
    public boolean checkClient(Socket cl){
        if (cl.isClosed()){
            System.out.println("Client" + cl.toString() + " đã offline");
            return true;
        }
        return false;
    }
}
