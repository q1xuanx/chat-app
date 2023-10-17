/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BLL.ServerControls.HandleClient;
import static BLL.ServerControls.mp;
import DAL.BlockModels;
import DAL.ListUserModels;
import PL.LoginViews;
import PL.MainViews;
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
import java.text.ParseException;
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
        System.out.println("Create server at " + sv.getLocalPort());
        System.out.println("Server start");
        mp = new HashMap<>();
        while (true) {
            System.out.println("Waiting for client...");
            Socket client = sv.accept();
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            String username = in.readUTF();
            System.out.println("[LOG]" + username + " new socket connect");
            HandleClient hc = new HandleClient(client, in, out);
            Thread t = new Thread(hc);
            t.start();
            ServerService ss = new ServerService();
            Thread t1 = new Thread(ss);
            t1.start();
        }
    }

    public class HandleClient implements Runnable {

        private Socket client;
        private DataInputStream in;
        private DataOutputStream out;

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
                            case "login":
                                String email = in.readUTF();
                                String pass = in.readUTF();
                                LoginControls lc = new LoginControls();
                                boolean checkLog = lc.checkLogin(email, pass);
                                out.writeBoolean(checkLog);
                                if (checkLog == true) {
                                    mp.put(email, client);
                                    System.out.println("[LOG] " + email + " đã đăng nhập");
                                }
                                break;
                            case "sign_up":
                                RegisterControls regis = new RegisterControls();
                                email = in.readUTF();
                                pass = in.readUTF();
                                String fullname = in.readUTF();
                                String gt = in.readUTF();
                                String ns = in.readUTF();
                                int checkSign = regis.SignUp(email, pass, fullname, gt, ns);
                                out.writeInt(checkSign);
                                if (checkSign == 1) {
                                    System.out.println("[LOG] Chào mừng thành viên mới: " + email);
                                }
                                break;
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
                                System.out.println("[LOG] " + userToSend + " đã gửi tin nhắn đến " + userSend);
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
                            case "check_friend":
                                String userNeedCheck = in.readUTF();
                                String userAdd = in.readUTF();
                                FriendControls fc = new FriendControls();
                                int checkIsFriend = fc.checkFriend(userNeedCheck, userAdd);
                                out.writeUTF("check_friend");
                                out.writeInt(checkIsFriend);
                                break;
                            case "user_out":
                                String username = in.readUTF();
                                System.out.println("[LOG]" + username + " has left");
                                Socket getOut = mp.get(username);
                                out = new DataOutputStream(getOut.getOutputStream());
                                out.writeUTF("kill_thread");
                                mp.remove(username);
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
                                System.out.println(receiveFile.toString());
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
                                    storeFile.storeMess(check, usersend, "[FILE] " + urlfile.toString(), curdat);
                                } else {
                                    storeFile.addNewChat(usersend, senduser);
                                    String ID = storeFile.checkChat(usersend, senduser);
                                    storeFile.storeMess(ID, usersend, "[FILE] " + urlfile.toString(), curdat);
                                }
                                String fileSend = file.getName();
                                sendFile(usersend, receiveFile, fileSend, curdat, "receive_file", urlfile.toString());
                                System.out.println("[LOG] " + usersend + " đã gửi tập tin đến " + senduser);
                                save.close();
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
                                    ArrayList<String> usernamee = new ArrayList<>();
                                    ArrayList<String> messageReceive = new ArrayList<>();
                                    ArrayList<String> timee = new ArrayList<>();
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
                                    for (int i = 0; i < usernamee.size(); i++) {
                                        out.writeUTF(usernamee.get(i));
                                        out.writeUTF(messageReceive.get(i));
                                        out.writeUTF(timee.get(i));
                                    }
                                }
                                break;
                            case "send_old_message_group":
                                String getGroupNameLoad = in.readUTF();
                                String userNeedLoad = in.readUTF();
                                GroupControls getOldMsgGroup = new GroupControls();
                                String IDGroupGetOld = getOldMsgGroup.getID(getGroupNameLoad);
                                ResultSet getOld = getOldMsgGroup.getOldMsg(IDGroupGetOld);
                                out.writeUTF("receive_old_message_group");
                                int sizeOfGroup = 0;
                                ArrayList<String> userNameFromGroup = new ArrayList<>();
                                ArrayList<String> detailsMess = new ArrayList<>();
                                ArrayList<String> timeUserSend = new ArrayList<>();
                                while (getOld.next()) {
                                    String userGr = getOld.getString(1);
                                    String detailM = getOld.getString(2);
                                    String timeSend = getOld.getString(3);
                                    if (userGr.equals(userNeedLoad)) {
                                        userGr = "You";
                                    }
                                    userNameFromGroup.add(userGr);
                                    detailsMess.add(detailM);
                                    timeUserSend.add(timeSend);
                                }
                                out.writeInt(userNameFromGroup.size());
                                for (int i = 0; i < userNameFromGroup.size(); i++) {
                                    out.writeUTF(userNameFromGroup.get(i));
                                    out.writeUTF(detailsMess.get(i));
                                    out.writeUTF(timeUserSend.get(i));
                                }
                                break;
                            case "leave_group":
                                String groupLeave = in.readUTF();
                                String userNeedLeave = in.readUTF();
                                GroupControls leaveGroup = new GroupControls();
                                String getIDToDel = leaveGroup.getID(groupLeave);
                                System.out.println(getIDToDel);
                                int leaved = leaveGroup.deleteUserInGroup(userNeedLeave, getIDToDel);
                                if (leaved == 1) {
                                    out.writeUTF("leave_group_success");
                                    out.writeUTF("Đã thoát nhóm " + groupLeave);
                                }
                                break;
                            case "user_to_add_group":
                                String userCreateGroup = in.readUTF();
                                BlockControls blc = new BlockControls();
                                UserControls ucs = new UserControls();
                                ArrayList<String> listUserCanAddToGroup = new ArrayList<>();
                                ResultSet userToAddGroup = ucs.getAllUser();
                                while (userToAddGroup.next()) {
                                    String nameUser = userToAddGroup.getString(1);
                                    if (nameUser.equals(userCreateGroup)) {
                                        continue;
                                    } else {
                                        ResultSet res = blc.checkBlock(userCreateGroup, nameUser);
                                        if (!res.next()) {
                                            listUserCanAddToGroup.add(nameUser);
                                        }
                                    }
                                }
                                out.writeUTF("get_user_to_add_group");
                                out.writeInt(listUserCanAddToGroup.size());
                                for (int i = 0; i < listUserCanAddToGroup.size(); i++) {
                                    out.writeUTF(listUserCanAddToGroup.get(i));
                                }
                                break;
                            case "send_pic":
                                String usersd = in.readUTF();
                                String userrei = in.readUTF();
                                String pathPic = in.readUTF();
                                String datesend = in.readUTF();
                                Socket sd = null;
                                for (Map.Entry<String, Socket> m : mp.entrySet()) {
                                    if (m.getKey().equals(userrei)) {
                                        sd = m.getValue();
                                        break;
                                    }
                                }
                                StoreMsgControls addDBPic = new StoreMsgControls();
                                String idchat = addDBPic.checkChat(usersd, userrei);
                                if (!idchat.equals("")) {
                                    addDBPic.storeMess(idchat, usersd, "[IMAGE]" + pathPic, datesend);
                                } else {
                                    addDBPic.addNewChat(usersd, userrei);
                                    String ID = addDBPic.checkChat(usersd, userrei);
                                    addDBPic.storeMess(ID, usersd, "[IMAGE]" + pathPic, datesend);
                                }
                                sendPic("rei_pic", usersd, sd, pathPic, datesend);
                                System.out.println("[LOG] " + usersd + " đã gửi nhãn dán đến " + userrei);
                                break;
                            case "send_pic_group":
                                StoreMsgControls addPicGroup = new StoreMsgControls();
                                GroupControls sendPicToGroup = new GroupControls();
                                String usersendGroup = in.readUTF();
                                String nameGroupRei = in.readUTF();
                                String pathPicGroup = in.readUTF();
                                String timeSendToGroup = in.readUTF();
                                ArrayList<Socket> listUserReiPic = new ArrayList<>();
                                String IDGroupReiPic = sendPicToGroup.getID(nameGroupRei);
                                ArrayList<String> userRei = sendPicToGroup.getMember(IDGroupReiPic);
                                for (int i = 0; i < userRei.size(); i++) {
                                    if (mp.get(userRei.get(i)) != null && !userRei.get(i).equals(usersendGroup)) {
                                        listUserReiPic.add(mp.get(userRei.get(i)));
                                    }
                                }
                                addPicGroup.storeMsgGroup(IDGroupReiPic, usersendGroup, "[IMAGE]" + pathPicGroup, timeSendToGroup);
                                sendPicGroup(usersendGroup, listUserReiPic, pathPicGroup, timeSendToGroup);
                                System.out.println("[LOG] " + usersendGroup + " đã gửi nhãn dán đến nhóm " + nameGroupRei);
                                break;
                            case "block_user":
                                ListUserModels rmvFriend = new ListUserModels();
                                action = "block_user";
                                String userSendBlock = in.readUTF();
                                String userBlock = in.readUTF();
                                BlockControls bl = new BlockControls();
                                int blocked = bl.addUserToBlock(userSendBlock, userBlock);
                                if (blocked == 1) {
                                    Socket block = null;
                                    Socket updateFr1 = null;
                                    Socket updateFr2 = null;
                                    int rmvfr1 = rmvFriend.RmvFriend(userSendBlock, userBlock);
                                    int rmvfr2 = rmvFriend.RmvFriend(userBlock, userSendBlock);    
                                    updateFr1 = mp.get(userSendBlock);
                                    updateFr2 = mp.get(userBlock);
                                    if (updateFr1 != null && rmvfr1 == 1){
                                        displayFriendList("handle_request1",updateFr1,userSendBlock);
                                    }
                                    if (updateFr2 != null && rmvfr2 == 1){
                                        displayFriendList("handle_request1",updateFr2,userBlock);
                                    }
                                    if (mp.get(userBlock) != null) {
                                        block = mp.get(userBlock);                                 
                                        sendBlockAct("user_blocked", block, userSendBlock);
                                    }
                                }
                                out.writeUTF(action);
                                out.writeInt(blocked);
                                out.writeUTF(userBlock);
                                break;
                            case "check_block_or_not":
                                String userNeed = in.readUTF();
                                String userCheck = in.readUTF();
                                System.out.println(userNeed + " " + userCheck);
                                BlockControls bc = new BlockControls();
                                ResultSet checkBlock = bc.checkBlock(userNeed, userCheck);
                                String revac = "check_block_or_not";
                                out.writeUTF(revac);
                                if (checkBlock.next()) {
                                    out.writeInt(1);
                                } else {
                                    out.writeInt(0);
                                }
                                break;
                            case "create_group":
                                GroupControls gs = new GroupControls();
                                String groupName = in.readUTF();
                                String userCreate = in.readUTF();
                                int createGroup = gs.createGroup(groupName);
                                String getID = gs.getID(groupName);
                                int sizeOfListUser = in.readInt();
                                int isCreated = 0;
                                int addMemberCreate = gs.addMember(userCreate, getID);
                                for (int i = 0; i < sizeOfListUser; i++) {
                                    String nameToAdd = in.readUTF();
                                    int addMem = gs.addMember(nameToAdd, getID);
                                    if (addMem == 1) {
                                        isCreated = 1;
                                    } else {
                                        isCreated = 0;
                                    }
                                }
                                out.writeUTF("create_group");
                                out.writeInt(isCreated);
                                if (isCreated == 1) {
                                    ArrayList<String> groupAvai = new ArrayList();
                                    ResultSet r = gs.getAllGroup();
                                    while (r.next()) {
                                        groupAvai.add(r.getString(1));
                                    }
                                    out.writeUTF("Tạo nhóm " + groupName + " thành công");
                                    out.writeInt(groupAvai.size());
                                    for (int i = 0; i < groupAvai.size(); i++) {
                                        out.writeUTF(groupAvai.get(i));
                                    }
                                    System.out.println("[LOG] " + userCreate + " đã tạo nhóm " + groupName);
                                } else {
                                    out.writeUTF("Xảy ra lỗi khi tạo nhóm");
                                }
                                break;
                            case "join_group_or_not":
                                GroupControls gsjoin = new GroupControls();
                                String nameGroupCheck = in.readUTF();
                                String userJoin = in.readUTF();
                                String ID = gsjoin.getID(nameGroupCheck);
                                ResultSet rese = gsjoin.checkJoinOrNot(userJoin, ID);
                                int blockornot = gsjoin.checkUserBlockGroup(userJoin, ID);
                                int joined = 0;
                                if (rese.next()) {
                                    joined = 1;
                                }
                                System.out.println(joined + " " + blockornot);
                                if (joined == 0 && blockornot == 0) {
                                    out.writeUTF("join_group_or_not");
                                    out.writeInt(joined);
                                    out.writeUTF(userJoin);
                                    out.writeUTF(nameGroupCheck);
                                    out.writeUTF(ID);
                                } else if (blockornot == 1) {
                                    out.writeUTF("blocked_group");
                                    out.writeUTF(nameGroupCheck);
                                } else if (joined == 1 && blockornot == 0) {
                                    out.writeUTF("group_joined");
                                    out.writeUTF(nameGroupCheck);
                                }
                                break;
                            case "new_member_join":
                                String newMem = in.readUTF();
                                String nameGroupJoin = in.readUTF();
                                String idToAdd = in.readUTF();
                                GroupControls gcsl = new GroupControls();
                                int addsuccess = gcsl.addMember(newMem, idToAdd);
                                if (addsuccess == 1) {
                                    out.writeUTF("join_success");
                                    out.writeUTF(nameGroupJoin);
                                }
                                break;
                            case "block_group":
                                BlockModels bmo = new BlockModels();
                                GroupControls gcls = new GroupControls();
                                String userblock = in.readUTF();
                                String groupBlock = in.readUTF();
                                String IDgroupBlock = gcls.getID(groupBlock);
                                int isDel = gcls.deleteUserInGroup(userblock, IDgroupBlock);
                                int isBlocked = bmo.blockGroup(userblock, IDgroupBlock);
                                if (isBlocked == 1 && isDel == 1) {
                                    out.writeUTF("blocked_group");
                                    out.writeUTF(groupBlock);
                                }
                                break;
                            case "join_group":
                                String userJoined = in.readUTF();
                                String groupJoin = in.readUTF();
                                GroupControls gcs = new GroupControls();
                                int addNewMem = gcs.addMember(userJoined, groupJoin);
                                if (addNewMem == 1) {
                                    out.writeUTF("join_success");
                                    out.writeUTF(groupJoin);
                                }
                                break;
                            case "display_group":
                                GroupControls gsdi = new GroupControls();
                                ResultSet rsg = gsdi.getAllGroup();
                                ArrayList<String> groupDisplay = new ArrayList<>();
                                while (rsg.next()) {
                                    groupDisplay.add(rsg.getString(1));
                                }
                                out.writeUTF("display_group");
                                out.writeInt(groupDisplay.size());
                                for (int i = 0; i < groupDisplay.size(); i++) {
                                    out.writeUTF(groupDisplay.get(i));
                                }
                                break;
                            case "send_file_group":
                                StoreMsgControls storemsggroup = new StoreMsgControls();
                                GroupControls controlsmsggroup = new GroupControls();
                                String nameOfGroup = in.readUTF();
                                String getIDGroup = controlsmsggroup.getID(nameOfGroup);
                                ArrayList<Socket> listMemReceiFile = new ArrayList<>();
                                ArrayList<String> listMemFile = controlsmsggroup.getMember(getIDGroup);
                                int byt = 0;
                                sizeOfFile = in.readLong();
                                fileName = in.readUTF();
                                curdat = in.readUTF();
                                usersend = in.readUTF();
                                path = "C:/Users/Admin/Desktop/ChatApp/ChatApplication/src/FileSave/Files/" + fileName;
                                File fileGroup = new File(path);
                                FileOutputStream saveFileGroup = new FileOutputStream(fileGroup);
                                byte[] read = new byte[4 * 1024];
                                while (sizeOfFile > 0 && (byt = in.read(read, 0, (int) Math.min(read.length, sizeOfFile))) != -1) {
                                    saveFileGroup.write(read, 0, byt);
                                    sizeOfFile -= byt;
                                }
                                for (int i = 0; i < listMemFile.size(); i++) {
                                    if (mp.get(listMemFile.get(i)) != null && !listMemFile.get(i).equals(usersend)) {
                                        listMemReceiFile.add(mp.get(listMemFile.get(i)));
                                    }
                                }
                                Path filePathToSendGroup = fileGroup.toPath();
                                URL urlfileSendGroup = filePathToSendGroup.toUri().toURL();
                                storemsggroup.storeMsgGroup(getIDGroup, usersend, "[FILE] " + urlfileSendGroup.toString(), curdat);
                                String fileSendGroup = fileGroup.getName();
                                sendFileToGroup(usersend, listMemReceiFile, fileSendGroup, urlfileSendGroup.toString(), curdat, nameOfGroup);
                                System.out.println("[LOG] " + usersend + " đã gửi tập tin đến nhóm " + nameOfGroup);
                                saveFileGroup.close();
                                break;
                            case "send_message_group":
                                ArrayList<Socket> listMem = new ArrayList<>();
                                ArrayList<String> memberOfGroup = new ArrayList<>();
                                StoreMsgControls grcs = new StoreMsgControls();
                                GroupControls smcs = new GroupControls();
                                String groupToSend = in.readUTF();
                                String messgroup = in.readUTF();
                                String nameusersend = in.readUTF();
                                String curDateGroup = in.readUTF();
                                String IDGroupChat = smcs.getID(groupToSend);
                                grcs.storeMsgGroup(IDGroupChat, nameusersend, messgroup, curDateGroup);
                                memberOfGroup = smcs.getMember(IDGroupChat);
                                for (int i = 0; i < memberOfGroup.size(); i++) {
                                    if (mp.get(memberOfGroup.get(i)) != null && !memberOfGroup.get(i).equals(nameusersend)) {
                                        listMem.add(mp.get(memberOfGroup.get(i)));
                                    }
                                }
                                System.out.println("[LOG] " + nameusersend + " đã gửi tin nhắn đến nhóm " + groupToSend);
                                sendMessageToGroup(messgroup, nameusersend, groupToSend, listMem, curDateGroup);
                                break;
                            case "handle_request1":
                                FriendControls fcs = new FriendControls();
                                int checkReques = in.readInt();
                                String userrev = in.readUTF();
                                String userse = in.readUTF();
                                if (checkReques == 0) {
                                    System.out.println("[LOG] " + userse + " đã kết bạn với " + userrev);
                                    fcs.addFriend(userse, userrev);
                                    fcs.addFriend(userrev, userse);
                                    Socket userreve = mp.get(userrev);
                                    Socket usersed = mp.get(userse);
                                    displayFriendList("handle_request1", userreve, userrev);
                                    displayFriendList("handle_request1", usersed, userse);
                                }
                                break;
                            case "handle_request2":
                                String userLoad = in.readUTF();
                                displayFriendList("handle_request2", client, userLoad);
                                ArrayList<String> ar = new ArrayList<>();
                                FriendControls fcc = new FriendControls();
                                ResultSet res = fcc.displayFriend(userLoad);
                                while (res.next()) {
                                    Socket getNoti = mp.get(res.getString(1));
                                    if (getNoti != null) {
                                        sendNotification("get_noti", "[BẠN BÈ] " + userLoad + " đã online ", getNoti);
                                    }
                                }
                                break;
                            default:

                                break;
                        }
                    } else {
                        client.close();
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
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

        //Send to group
        public void sendMessageToGroup(String msg, String username, String groupName, ArrayList<Socket> arr, String curdate) throws IOException {
            DataOutputStream out;
            for (int i = 0; i < arr.size(); i++) {
                out = new DataOutputStream(arr.get(i).getOutputStream());
                out.writeUTF("receive_msg_group");
                out.writeUTF(msg);
                out.writeUTF(curdate);
                out.writeUTF(username);
                out.writeUTF(groupName);
            }
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

        public void sendFileToGroup(String username, ArrayList<Socket> arr, String fileName, String path, String curdate, String groupName) throws IOException {
            DataOutputStream out;
            for (int i = 0; i < arr.size(); i++) {
                out = new DataOutputStream(arr.get(i).getOutputStream());
                out.writeUTF("receive_file_group");
                out.writeUTF(username);
                out.writeUTF(fileName);
                out.writeUTF(curdate);
                out.writeUTF(path);
                out.writeUTF(groupName);
            }
        }

        public void sendPic(String action, String username, Socket s, String pathPic, String curdate) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(action);
            out.writeUTF(username);
            out.writeUTF(pathPic);
            out.writeUTF(curdate);
            String note = "Bạn có tin nhắn mới từ: " + username;
            out.writeUTF(note);
        }

        public void sendPicGroup(String username, ArrayList<Socket> arr, String pathPic, String curdate) throws IOException {
            DataOutputStream out;
            for (int i = 0; i < arr.size(); i++) {
                out = new DataOutputStream(arr.get(i).getOutputStream());
                out.writeUTF("send_pic_group");
                out.writeUTF(username);
                out.writeUTF(pathPic);
                out.writeUTF(curdate);
            }
        }

        public void sendBlockAct(String act, Socket s, String userSendBlock) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(act);
            String mess = "Người dùng không khả dụng";
            out.writeUTF(mess);
            out.writeUTF(userSendBlock);
        }

        public void displayFriendList(String act, Socket s, String userDisplay) throws IOException, ClassNotFoundException, SQLException {
            FriendControls fc = new FriendControls();
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(act);
            ResultSet getInfo = fc.displayFriend(userDisplay);
            ArrayList<String> arr = new ArrayList();
            while (getInfo.next()) {
                arr.add(getInfo.getString(1));
            }
            out.writeInt(arr.size());
            for (int i = 0; i < arr.size(); i++) {
                out.writeUTF(arr.get(i));
            }
        }

        public void sendNotification(String act, String msg, Socket s) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(act);
            out.writeUTF(msg);
        }
    }

    public class ServerService implements Runnable {

        @Override
        public void run() {
            while (true) {
                Scanner sc = new Scanner(System.in);
                System.out.println("-help for list command");
                String choice = sc.nextLine();
                switch (choice) {
                    case "-help":
                        System.out.println("-userlist: Liệt kê danh sách user online và tổng user");
                        System.out.println("-block + tên user: chặn một user");
                        System.out.println("-broadcast: Gửi tin nhắn đến cho các user ");
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
                        try {
                            System.out.println("Nhập tên user cần block");
                            String s = sc.nextLine();
                            BanControls bcs = new BanControls();
                            bcs.BanUser(s);
                            Socket getBan = mp.get(s);
                            if (getBan != null) {
                                out = new DataOutputStream(getBan.getOutputStream());
                                out.writeUTF("banned");
                                out.writeUTF("Bạn đã bị khóa tài khoản!");
                            }
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "-broadcast":
                        System.out.println("Nhập tin nhắn: ");
                        String broad = sc.nextLine();
                        for (Map.Entry<String, Socket> m : mp.entrySet()) {
                            try {
                                broadCastMsg(broad, m.getValue());
                            } catch (IOException ex) {
                                Logger.getLogger(ServerControls.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                }
            }
        }

        public void broadCastMsg(String msg, Socket s) throws IOException {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF("broad_cast");
            out.writeUTF(msg);
        }
    }
}
