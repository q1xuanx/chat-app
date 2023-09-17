/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Admin
 */
public class ClientsControls {
    public Socket client; 
    public DataInputStream input;
    public DataOutputStream output;
    
    public void joinServer(String message) throws IOException{
        client = new Socket("localhost",7777);
        input = new DataInputStream(client.getInputStream());
        output = new DataOutputStream(client.getOutputStream());
        output.writeUTF(message);
    }
    public void SendMessage(String s) throws IOException{
        output.writeUTF(s);
    }
    public String ReceiveMessage() throws IOException{
        return input.readUTF();
    }
}
