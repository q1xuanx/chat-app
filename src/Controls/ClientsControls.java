/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import Views.MainViews;
import static Views.MainViews.currentChatWith;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Admin
 */
public class ClientsControls {

    public static class ServerHandler implements Runnable {

        private static Socket client;
        private static DataOutputStream out;
        private static DataInputStream in;
        private static JTextArea txtdisplay;
        private static JList userOnline;
        private static String username;
        private static JTextArea thongBao;

        public ServerHandler(Socket client, JTextArea txtdisplay, JList userOnline, String username, JTextArea thongBao) {
            ServerHandler.client = client;
            ServerHandler.txtdisplay = txtdisplay;
            ServerHandler.userOnline = userOnline;
            ServerHandler.username = username;
            ServerHandler.thongBao = thongBao;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                while (true) {
                    String choice = in.readUTF();
                    switch (choice) {
                        case "receive_msg":
                            in = new DataInputStream(client.getInputStream());
                            String userName = in.readUTF();
                            String msg = in.readUTF();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            LocalDateTime date = LocalDateTime.now();
                            String curdate = date.format(formatter);
                            if (currentChatWith.equals(userName)) {
                                txtdisplay.append(userName + ": " + msg + "\n" + curdate + "\n");
                                txtdisplay.setForeground(Color.blue);
                            } else {
                                thongBao.append(in.readUTF() + "\n");
                            }
                            break;
                        case "get_user_online":
                            int sizeOfMap = Integer.parseInt(in.readUTF());
                            DefaultListModel ls = new DefaultListModel();
                            while (sizeOfMap > 0) {
                                ls.addElement(in.readUTF());
                                sizeOfMap--;
                            }
                            ls.removeElement(username);
                            userOnline.setModel(ls);
                            break;
                        case "receive_request":
                            String requestFrom = in.readUTF();
                            JOptionPane.showConfirmDialog(null, requestFrom + " Muốn kết bạn với bạn ");
                            break;
                        case "kill_thread":
                            client.close();
                            break;
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(ClientsControls.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
