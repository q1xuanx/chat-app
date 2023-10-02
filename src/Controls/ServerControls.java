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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                                String curdate = in.readUTF();
                                StoreMsgControls addDB = new StoreMsgControls();
                                String IDCHAT = addDB.checkChat(userSend, userToSend);
                                if (!IDCHAT.equals("")) {
                                    addDB.storeMess(IDCHAT, userSend, msg, curdate);
                                } else {
                                    addDB.addNewChat(userSend, userToSend);
                                    String ID = addDB.checkChat(userSend, userToSend);
                                    addDB.storeMess(ID, userSend, msg, curdate);
                                }
                                sendMessage(msg, userSend, temp, curdate);
                                break;
                            case "get_user_online":
                                String action = "get_user_online";
                                out.writeUTF(action);
                                int sizeOfMap = mp.size();
                                out.writeUTF(String.valueOf(sizeOfMap));
                                for (Map.Entry<String, Socket> m : mp.entrySet()) {
                                    out.writeUTF(m.getKey());
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
                                for (Map.Entry<String, Socket> m : mp.entrySet()) {
                                    if (m.getKey().equals(userNeedToSend)) {
                                        clientNeedToSend = m.getValue();
                                        break;
                                    }
                                }
                                String userSended = in.readUTF();
                                sendRequestFriend(clientNeedToSend, "receive_request", userSended);
                                break;
                            case "send_file":
                                String senduser = in.readUTF();
                                Socket receiveFile = null;
                                for (Map.Entry<String, Socket> m : mp.entrySet()) {
                                    if (m.getKey().equals(senduser)) {
                                        receiveFile = m.getValue();
                                        break;
                                    }
                                }
                                int bytes = 0;
                                StoreMsgControls storeFile = new StoreMsgControls();
                                long sizeOfFile = in.readLong();
                                String fileName = in.readUTF();
                                String curdat = in.readUTF();
                                String usersend = in.readUTF();
                                String check = storeFile.checkChat(senduser, usersend);
                                String path = "C:/Users/Admin/Desktop/ChatApp/ChatApplication/src/FileSave/Files/" + fileName;
                                File file = new File(path);
                                FileOutputStream save = new FileOutputStream(file);
                                byte[] buffer = new byte[4 * 1024];
                                while (sizeOfFile > 0 && (bytes = in.read(buffer, 0, (int) Math.min(buffer.length, sizeOfFile))) != -1) {
                                    save.write(buffer, 0, bytes);
                                    sizeOfFile -= bytes;
                                }
                                Path filePath = file.toPath();
                                URL urlfile = filePath.toUri().toURL();
                                if (!check.equals("")) {
                                    storeFile.storeMess(check, usersend,"[FILE] " + urlfile.toString(),curdat);
                                } else {
                                    storeFile.addNewChat(usersend, senduser);
                                    String ID = storeFile.checkChat(usersend, senduser);
                                    storeFile.storeMess(ID, usersend,"[FILE] " + urlfile.toString(), curdat);
                                }
                                String fileSend = file.getName();
                                sendFile(usersend, receiveFile, fileSend, curdat, "receive_file", urlfile.toString());
                                break;
                            case "send_old_message":
                                String username1 = in.readUTF();
                                String username2 = in.readUTF();
                                StoreMsgControls sendOldMsg = new StoreMsgControls();
                                String s = sendOldMsg.checkChat(username1, username2);
                                if (!s.equals("")) {
                                    out.writeUTF("receive_old_message");
                                    ResultSet res = sendOldMsg.takeOldMessage(s);
                                    int size = 0;
                                    ArrayList<String> usernamee = new ArrayList<String>();
                                    ArrayList<String> messageReceive = new ArrayList<String>();
                                    ArrayList<String> timee = new ArrayList<String>();
                                    while (res.next()) {
                                        String user = res.getString(1);
                                        String text = res.getString(2);
                                        String time = res.getString(3);
                                        if (username2.equals(user)) {
                                            user = "You";
                                        }
                                        usernamee.add(user);
                                        messageReceive.add(text);
                                        timee.add(time);
                                    }
                                    out.writeInt(usernamee.size());
                                    for (int i = 0; i < usernamee.size(); i++){
                                        out.writeUTF(usernamee.get(i));
                                        out.writeUTF(messageReceive.get(i));
                                        out.writeUTF(timee.get(i));
                                    }
                                }
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
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //Send to user
        public void sendMessage(String msg, String userToSend, Socket s, String curdate) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String action = "receive_msg";
            out.writeUTF(action);
            out.writeUTF(userToSend);
            out.writeUTF(msg);
            out.writeUTF(curdate);
            String note = "Bạn có tin nhắn mới từ: " + userToSend;
            out.writeUTF(note);
        }

        public void sendRequestFriend(Socket s, String action, String userSend) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(action);
            out.writeUTF(userSend);
        }

        public void sendFile(String username, Socket s, String fileName, String curdate, String action, String path) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(action);
            out.writeUTF(username);
            out.writeUTF(fileName);
            out.writeUTF(curdate);
            out.writeUTF(path);
            String note = "Bạn có tin nhắn mới từ: " + username;
            out.writeUTF(note);
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
