/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import PL.CreateGroupViews;
import PL.MainViews;
import static PL.MainViews.currentChatWith;
import static PL.MainViews.userSend;
import java.awt.Color;
import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author Admin
 */
public class ClientsControls {

    public static class ServerHandler implements Runnable {

        private static Socket client;
        private static DataOutputStream out;
        private static DataInputStream in;
        private static JList userOnline;
        private static String username;
        private static JTextArea thongBao;

        public ServerHandler(Socket client, JList userOnline, String username, JTextArea thongBao) {
            ServerHandler.client = client;
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
                    if (client.isConnected()) {
                        String choice = in.readUTF();
                        switch (choice) {
                            case "banned":
                                String getMessBan = in.readUTF();
                                JOptionPane.showMessageDialog(null, getMessBan);
                                System.exit(0);
                                break;
                            case "broad_cast":
                                String messBroad = in.readUTF();
                                JOptionPane.showMessageDialog(null, messBroad);
                                break;
                            case "receive_msg":
                                in = new DataInputStream(client.getInputStream());
                                String userName = in.readUTF();
                                String msg = in.readUTF();
                                String curdate = in.readUTF();
                                String notemess = in.readUTF();
                                if (MainViews.currentChatWith.equals(userName) && !MainViews.currentChatWith.equals("None")) {
                                    MainViews.displayMessage.setContentType("text/html");
                                    HTMLDocument display = (HTMLDocument) MainViews.displayMessage.getDocument();
                                    String s = userName + ": " + msg + "<br>" + curdate + "<br>";
                                    display.insertAfterEnd(display.getCharacterElement(display.getLength()), s);
                                    MainViews.displayMessage.setForeground(Color.blue);
                                } else {
                                    thongBao.append(notemess + "\n");
                                }
                                break;
                            case "receive_msg_group":
                                in = new DataInputStream(client.getInputStream());
                                String messgroup = in.readUTF();
                                String curdategroup = in.readUTF();
                                String userSendToGroup = in.readUTF();
                                String nameGroup = in.readUTF();
                                if (MainViews.currentChatWith.equals(nameGroup)) {
                                    MainViews.displayMessageGroup.setContentType("text/html");
                                    HTMLDocument display = (HTMLDocument) MainViews.displayMessageGroup.getDocument();
                                    String s = userSendToGroup + ": " + messgroup + "<br>" + curdategroup + "<br>";
                                    display.insertAfterEnd(display.getCharacterElement(display.getLength()), s);
                                    MainViews.displayMessageGroup.setForeground(Color.blue);
                                }
                                break;
                            case "get_user_to_add_group":
                                int sizeUserAddGroup = in.readInt();
                                DefaultListModel dfms = new DefaultListModel();
                                for (int i = 0; i < sizeUserAddGroup; i++) {
                                    dfms.addElement(in.readUTF());
                                }
                                CreateGroupViews.listUserAddGroup.setModel(dfms);
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
                                int checkReques = JOptionPane.showConfirmDialog(null, requestFrom + " muốn kết bạn với bạn");
                                out.writeUTF("handle_request");
                                out.writeInt(checkReques);
                                out.writeUTF(username);
                                out.writeUTF(requestFrom);
                                break;
                            case "check_friend":
                                int isFriend = in.readInt();
                                if (isFriend == 1) {
                                    JOptionPane.showMessageDialog(null, "Hai bạn đã là bạn bè");
                                } else {
                                    out.writeUTF("send_request_friend");
                                    out.writeUTF(MainViews.currentChatWith);
                                    out.writeUTF(MainViews.username);
                                }
                                break;
                            case "handl_request1":
                                int sizeOfListFriend = in.readInt();
                                DefaultListModel dfa = new DefaultListModel();
                                for (int i = 0; i < sizeOfListFriend; i++) {
                                    dfa.addElement(in.readUTF());
                                }
                                MainViews.listFriend.setModel(dfa);
                                break;
                            case "handle_request2":
                                sizeOfListFriend = in.readInt();
                                dfa = new DefaultListModel();
                                for (int i = 0; i < sizeOfListFriend; i++) {
                                    dfa.addElement(in.readUTF());
                                }
                                MainViews.listFriend.setModel(dfa);
                                break;
                            case "join_group_or_not":
                                int joined = in.readInt();
                                String userWantJoin = in.readUTF();
                                String nameGroupJoin = in.readUTF();
                                String IDGroup = in.readUTF();
                                int isJoined = 0;
                                Object[] options = {"Tham gia nhóm", "Chặn nhóm", "Thoát"};
                                if (joined == 0) {
                                    isJoined = JOptionPane.showOptionDialog(null, "Bạn chưa vào nhóm này bạn có muốn tham gia hay chặn nhóm ?", "ASK", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                    if (isJoined == 0) {
                                        out.writeUTF("new_member_join");
                                        out.writeUTF(userWantJoin);
                                        out.writeUTF(nameGroupJoin);
                                        out.writeUTF(IDGroup);
                                    } else if (isJoined == 1) {
                                        out.writeUTF("block_group");
                                        out.writeUTF(userWantJoin);
                                        out.writeUTF(nameGroupJoin);
                                    }
                                }
                                break;
                            case "group_joined":
                                String nameGroupJoined = in.readUTF();
                                out.writeUTF("send_old_message_group");
                                out.writeUTF(nameGroupJoined);
                                out.writeUTF(username);
                                MainViews.nameofgroup.setText(nameGroupJoined);
                                MainViews.currentChatWith = nameGroupJoined;
                                break;
                            case "blocked_group":
                                String nameGrouphasBlock = in.readUTF();
                                JOptionPane.showMessageDialog(null, "Bạn đã chặn nhóm " + nameGrouphasBlock);
                                MainViews.nameofgroup.setText("NAME OF GROUP");
                                MainViews.displayMessageGroup.setText(null);
                                MainViews.currentChatWith = "";
                                break;
                            case "leave_group_success":
                                String leaved = in.readUTF();
                                JOptionPane.showMessageDialog(null, leaved);
                                MainViews.nameofgroup.setText("NAME OF GROUP");
                                MainViews.displayMessageGroup.setText(null);
                                MainViews.currentChatWith = "";
                                break;
                            case "join_success":
                                String groupHasJoined = in.readUTF();
                                JOptionPane.showMessageDialog(null, "Bạn đã tham gia nhóm " + groupHasJoined);
                                MainViews.nameofgroup.setText(groupHasJoined);
                                MainViews.currentChatWith = groupHasJoined;
                                break;
                            case "receive_old_message":
                                int size = in.readInt();
                                System.out.println(size);
                                if (size == 0) {
                                    MainViews.displayMessage.setText(null);
                                } else {
                                    while (size > 0) {
                                        String user = in.readUTF();
                                        String message = in.readUTF();
                                        String timesend = in.readUTF();
                                        if (message.contains("[FILE]")) {
                                            MainViews.displayMessage.setContentType("text/html");
                                            String start = "[FILE]";
                                            String filepath = message.replace(start, "");
                                            File filesave = new File(filepath);
                                            URL urls = new URL(filepath);
                                            String sendFile = user + ": <a href='" + urls + "'>" + filesave.getName() + "</a>" + "<br>" + timesend + "<br>";
                                            HTMLDocument doc = (HTMLDocument) MainViews.displayMessage.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                            MainViews.displayMessage.setForeground(Color.blue);
                                            MainViews.displayMessage.addHyperlinkListener(new MyHyperLinkControls());
                                        } else if (message.contains("[IMAGE]")) {
                                            MainViews.displayMessage.setContentType("text/html");
                                            String start = "[IMAGE]";
                                            String file = message.replace(start, "");
                                            StyledDocument doc = (StyledDocument) MainViews.displayMessage.getDocument();
                                            Style st = doc.addStyle("Image", null);
                                            StyleConstants.setIcon(st, new ImageIcon(file));
                                            doc.insertString(doc.getLength(), "\n", null);
                                            doc.insertString(doc.getLength(), user, null);
                                            doc.insertString(doc.getLength(), "Image", st);
                                            doc.insertString(doc.getLength(), "\n", null);
                                            doc.insertString(doc.getLength(), timesend, null);
                                            doc.insertString(doc.getLength(), "\n", null);
                                        } else {
                                            MainViews.displayMessage.setContentType("text/html");
                                            String s = user + ": " + message + "<br>" + timesend + "<br>";
                                            HTMLDocument doc = (HTMLDocument) MainViews.displayMessage.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), s);
                                        }
                                        size--;
                                        if (size == 0) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case "receive_old_message_group":
                                int sizeOfGroup = in.readInt();
                                System.out.println(sizeOfGroup);
                                if (sizeOfGroup == 0) {
                                    MainViews.displayMessageGroup.setText(null);
                                } else {
                                    while (sizeOfGroup > 0) {
                                        String user = in.readUTF();
                                        String message = in.readUTF();
                                        String timesend = in.readUTF();
                                        if (message.contains("[FILE]")) {
                                            MainViews.displayMessageGroup.setContentType("text/html");
                                            String startW = "[FILE]";
                                            String filepathG = message.replace(startW, "");
                                            File filesaveG = new File(filepathG);
                                            URL urls = new URL(filepathG);
                                            String sendFile = user + ": <a href='" + urls + "'>" + filesaveG.getName() + "</a>" + "<br>" + timesend + "<br>";
                                            HTMLDocument document = (HTMLDocument) MainViews.displayMessageGroup.getDocument();
                                            document.insertAfterEnd(document.getCharacterElement(document.getLength()), sendFile);
                                            MainViews.displayMessageGroup.setForeground(Color.blue);
                                            MainViews.displayMessageGroup.addHyperlinkListener(new MyHyperLinkControls());
                                        } else if (message.contains("[IMAGE]")) {
                                            MainViews.displayMessageGroup.setContentType("text/html");
                                            String startW = "[IMAGE]";
                                            String filepathG = message.replace(startW, "");
                                            StyledDocument document = (StyledDocument) MainViews.displayMessageGroup.getDocument();
                                            Style st = document.addStyle("Image", null);
                                            StyleConstants.setIcon(st, new ImageIcon(filepathG));
                                            document.insertString(document.getLength(), "\n", null);
                                            document.insertString(document.getLength(), user, null);
                                            document.insertString(document.getLength(), "Image", st);
                                            document.insertString(document.getLength(), "\n", null);
                                            document.insertString(document.getLength(), timesend, null);
                                            document.insertString(document.getLength(), "\n", null);
                                        } else {
                                            MainViews.displayMessageGroup.setContentType("text/html");
                                            String s = user + ": " + message + "<br>" + timesend + "<br>";
                                            HTMLDocument doc = (HTMLDocument) MainViews.displayMessageGroup.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), s);
                                        }
                                        sizeOfGroup--;
                                        if (sizeOfGroup == 0) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case "kill_thread":
                                // Imple....
                                return;
                            case "receive_file":
                                in = new DataInputStream(client.getInputStream());
                                String username = in.readUTF();
                                String filename = in.readUTF();
                                String curdat = in.readUTF();
                                String urlString = in.readUTF();
                                String note = in.readUTF();
                                URL url = new URL(urlString);
                                if (MainViews.currentChatWith.equals(username) && !MainViews.currentChatWith.equals("None")) {
                                    MainViews.displayMessage.setContentType("text/html");
                                    String sendFile = username + ": <a href='" + url + "'>" + filename + "</a>" + "<br>" + curdat + "<br>";
                                    HTMLDocument doc = (HTMLDocument) MainViews.displayMessage.getDocument();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                    MainViews.displayMessage.setForeground(Color.blue);
                                    MainViews.displayMessage.addHyperlinkListener(new MyHyperLinkControls());
                                } else {
                                    thongBao.append(note + "\n");
                                }
                                break;
                            case "receive_file_group":
                                in = new DataInputStream(client.getInputStream());
                                String memReiFile = in.readUTF();
                                String nameFileGroup = in.readUTF();
                                String datecurrent = in.readUTF();
                                String urlFileToConnect = in.readUTF();
                                String getNameGroup = in.readUTF();
                                URL urlGroupFile = new URL(urlFileToConnect);
                                if (MainViews.currentChatWith.equals(getNameGroup)) {
                                    MainViews.displayMessageGroup.setContentType("text/html");
                                    String sendFile = memReiFile + ": <a href='" + urlFileToConnect + "'>" + nameFileGroup + "</a>" + "<br>" + datecurrent + "<br>";
                                    HTMLDocument doc = (HTMLDocument) MainViews.displayMessageGroup.getDocument();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                    MainViews.displayMessageGroup.setForeground(Color.blue);
                                    MainViews.displayMessageGroup.addHyperlinkListener(new MyHyperLinkControls());
                                }
                                break;
                            case "rei_pic":
                                String nameuser = in.readUTF();
                                String pathPic = in.readUTF();
                                String datesend = in.readUTF();
                                MainViews.displayMessage.setContentType("text/html");
                                StyledDocument doc = (StyledDocument) MainViews.displayMessage.getDocument();
                                Style style = doc.addStyle("Image", null);
                                StyleConstants.setIcon(style, new ImageIcon(pathPic));
                                try {
                                    doc.insertString(doc.getLength(), nameuser, null);
                                    doc.insertString(doc.getLength(), "Image", style);
                                    doc.insertString(doc.getLength(), "\n", null);
                                    doc.insertString(doc.getLength(), datesend, null);
                                    doc.insertString(doc.getLength(), "\n", null);
                                } catch (BadLocationException ex) {
                                    Logger.getLogger(MainViews.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case "send_pic_group":
                                String nameUserSendGroup = in.readUTF();
                                String pathPicSendGr = in.readUTF();
                                String timeSend = in.readUTF();
                                MainViews.displayMessageGroup.setContentType("text/html");
                                StyledDocument docc = (StyledDocument) MainViews.displayMessageGroup.getDocument();
                                Style stylee = docc.addStyle("Image", null);
                                StyleConstants.setIcon(stylee, new ImageIcon(pathPicSendGr));
                                try {
                                    docc.insertString(docc.getLength(), nameUserSendGroup, null);
                                    docc.insertString(docc.getLength(), "Image", stylee);
                                    docc.insertString(docc.getLength(), "\n", null);
                                    docc.insertString(docc.getLength(), timeSend, null);
                                    docc.insertString(docc.getLength(), "\n", null);
                                } catch (BadLocationException ex) {
                                    Logger.getLogger(MainViews.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case "block_user":
                                int res = in.readInt();
                                String userToBlock = in.readUTF();
                                if (res == 1) {
                                    JOptionPane.showMessageDialog(null, "Bạn đã chặn người dùng " + userToBlock);
                                    MainViews.userSend.setText("None");
                                    MainViews.displayMessage.setText("");
                                }
                                break;
                            case "check_block_or_not":
                                int resl = in.readInt();
                                if (resl == 1) {
                                    JOptionPane.showMessageDialog(null, "Người dùng không khả dụng");
                                } else {
                                    MainViews.userSend.setText(MainViews.userOnline.getSelectedValue());
                                    out.writeUTF("send_old_message");
                                    out.writeUTF(userSend.getText());
                                    out.writeUTF(ServerHandler.username);
                                }
                                break;
                            case "display_group":
                                int isSize = in.readInt();
                                DefaultListModel displayGroup = new DefaultListModel();
                                for (int i = 0; i < isSize; i++) {
                                    displayGroup.addElement(in.readUTF());
                                }
                                MainViews.listGroup.setModel(displayGroup);
                                break;
                            case "create_group":
                                int isCreate = in.readInt();
                                if (isCreate == 1) {
                                    String created = in.readUTF();
                                    int numberOfGroup = in.readInt();
                                    DefaultListModel dlm = new DefaultListModel();
                                    for (int i = 0; i < numberOfGroup; i++) {
                                        dlm.addElement(in.readUTF());
                                    }
                                    MainViews.listGroup.setModel(dlm);
                                    JOptionPane.showMessageDialog(null, "Tạo nhóm thành công");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Xảy ra lỗi khi tạo nhóm");
                                }
                                break;
                            case "user_blocked":
                                String readMess = in.readUTF();
                                String userSendBl = in.readUTF();
                                if (MainViews.userSend.getText().equals(userSendBl)) {
                                    JOptionPane.showMessageDialog(null, readMess);
                                    MainViews.userSend.setText("None");
                                    MainViews.displayMessage.setText("");
                                }
                                break;
                            case "get_noti":
                                MainViews.txtnote.append(in.readUTF() + "\n");
                                break;
                        }
                    } else {

                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(ClientsControls.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadLocationException ex) {
                Logger.getLogger(ClientsControls.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
