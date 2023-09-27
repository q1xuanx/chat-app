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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class ClientsControls {

    public static class ServerHandler implements Runnable {

        private Socket client;
        DataOutputStream out;
        DataInputStream in;

        public ServerHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                if (in.readUTF() != null) {
                    String choice = in.readUTF();
                    switch (choice) {
                        case "receive_msg":
                            in = new DataInputStream(client.getInputStream());
                            String msg = in.readUTF();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientsControls.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
