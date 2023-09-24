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
    private static Socket client; 
    private static DataInputStream input;
    private static DataOutputStream output;
    
    public String readMessage(Socket client, String msg) throws IOException{
        input = new DataInputStream(client.getInputStream());
        return input.readUTF();
    }
}
