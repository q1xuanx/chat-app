/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import Controls.ServerControls.HandleClient;
import Models.ListUserModels;
import Views.LoginViews;
import Views.MainViews;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author Admin
 */
public class ServerControls {

    public static Map<String, Socket> mp;
    public ServerSocket sv;
    public Socket client;
    public DataOutputStream out;
    public DataInputStream in;
    public String username = "";

    public ServerControls() throws IOException {
        sv = new ServerSocket(7777);
        System.out.println("Server start");
        mp = new HashMap<>();
        while (true) {
            System.out.println("Waiting for client...");
            Socket client = sv.accept();
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            String username = in.readUTF();
            System.out.println("[LOG]" + username + " has login to server");
            mp.put(username, client);
            HandleClient hc = new HandleClient(client, in, out);
            Thread t = new Thread(hc);
            t.start();
            ServerService ss = new ServerService();
            Thread t1 = new Thread(ss);
            t1.start();
        }
    }

    public static void main(String args[]) throws IOException {
        ServerControls sv = new ServerControls();
    }

    public class HandleClient implements Runnable {

        private Socket client;
        private DataInputStream in;
        private DataOutputStream out;
        private MainViews mv;

        public HandleClient(Socket client, DataInputStream in, DataOutputStream out) {
            this.client = client;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String choice = in.readUTF();
                    if (client.isConnected()) {
                        switch (choice) {
                            case "send_message":
                                String userToSend = in.readUTF();
                                String msg = in.readUTF();
                                Socket temp = null;
                                for (Map.Entry<String, Socket> en : mp.entrySet()) {
                                    if (en.getKey().equals(userToSend)) {
                                        temp = en.getValue();
                                        break;
                                    }
                                }
                                String userSend = in.readUTF();
                                sendMessage(msg, userSend, temp);
                                break;
                            case "get_user_online":
                                String action = "get_user_online";
                                out.writeUTF(action);
                                int sizeOfMap = mp.size();
                                out.writeUTF(String.valueOf(sizeOfMap));
                                for (Map.Entry<String, Socket> m : mp.entrySet()) {
                                    out.writeUTF(m.getKey());
                                    System.out.println(m.getKey());
                                }
                                break;
                            case "user_out":
                                String username = in.readUTF();
                                System.out.println("[LOG]" + username + " has left");
                                mp.remove(username);
                                client.close();
                                out.writeUTF("kill_thread");
                                break;
                            case "send_request_friend":
                                String userNeedToSend = in.readUTF();
                                Socket clientNeedToSend = null;
                                for (Map.Entry<String, Socket> m : mp.entrySet()){
                                    if (m.getKey().equals(userNeedToSend)){
                                        clientNeedToSend = m.getValue();
                                        break;
                                    }
                                }
                                String userSended = in.readUTF();
                                sendRequestFriend(clientNeedToSend,"receive_request", userSended);
                                break;
                            default:

                                break;
                        }
                    } else {
                        sv.close();
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //Send to user
        public void sendMessage(String msg, String userToSend, Socket s) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String action = "receive_msg";
            out.writeUTF(action);
            out.writeUTF(userToSend);
            out.writeUTF(msg);
            String note = "Bạn có tin nhắn mới từ: " + userToSend;
            out.writeUTF(note);
        }
        public void sendRequestFriend(Socket s, String action, String userSend) throws IOException{
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(action);
            out.writeUTF(userSend);
        }
    }

    public class ServerService implements Runnable {

        @Override
        public void run() {
            while (true) {
                Scanner sc = new Scanner(System.in);
                System.out.println("-help for list command");
                String choice = sc.next();
                switch (choice) {
                    case "-help":
                        System.out.println("-userlist: Liệt kê danh sách user online và tổng user");
                        System.out.println("-block + tên user: chặn một user");
                        break;
                    case "-userlist":
                        ListUserModels ls = new ListUserModels();
                         {
                            try {
                                System.out.println("Số lượng user online: " + mp.size() + "| Tổng số user " + ls.totalUser());
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    case "-block":
                        System.out.println("Nhập tên user cần block");
                        String s = sc.nextLine();
                        mp.remove(s);
                        System.out.println("Đã block user");
                        break;
                }
            }
        }
    }
}
