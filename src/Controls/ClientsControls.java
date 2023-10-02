/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import Views.MainViews;
import static Views.MainViews.currentChatWith;
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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
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
        private static JTextPane txtdisplay;
        private static JList userOnline;
        private static String username;
        private static JTextArea thongBao;

        public ServerHandler(Socket client, JTextPane txtdisplay, JList userOnline, String username, JTextArea thongBao) {
            ServerHandler.client = client;

            ServerHandler.txtdisplay = txtdisplay;
            ServerHandler.userOnline = userOnline;
            ServerHandler.username = username;
            ServerHandler.thongBao = thongBao;
            txtdisplay.setEditable(false);
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
                            case "receive_msg":
                                in = new DataInputStream(client.getInputStream());
                                String userName = in.readUTF();
                                String msg = in.readUTF();
                                String curdate = in.readUTF();
                                System.out.println(userName);
                                if (MainViews.currentChatWith.equals(userName) && !MainViews.currentChatWith.equals("None")) {
                                    txtdisplay.setContentType("text/html");
                                    HTMLDocument display = (HTMLDocument) txtdisplay.getDocument();
                                    String s = "<br><p>" + userName + ": " + msg + "<br>" + curdate + "</p>";
                                    display.insertAfterEnd(display.getCharacterElement(display.getLength()), s);
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
                                MainViews.accept = JOptionPane.showConfirmDialog(null, requestFrom + " Muốn kết bạn với bạn ");
                                if (MainViews.accept == 0) {
                                    JOptionPane.showMessageDialog(null, "Các bạn đã thành bạn bè");
                                }
                                break;
                            case "receive_old_message":
                                int size = in.readInt();
                                System.out.println(size);
                                if (size == 0) {
                                    txtdisplay.setText(null);
                                } else {
                                    while (size > 0) {
                                        String user = in.readUTF();
                                        String message = in.readUTF();
                                        String timesend = in.readUTF();
                                        if (message.contains("[FILE]")) {
                                            txtdisplay.setContentType("text/html");
                                            String start = "[FILE]";
                                            String filepath = message.replace(start, "");
                                            File filesave = new File(filepath);
                                            URL urls = new URL(filepath);
                                            String sendFile = user + ": <a href='" + urls + "'>" + filesave.getName() + "</a>" + "<br>" + timesend;
                                            HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                            txtdisplay.setForeground(Color.blue);
                                            txtdisplay.addHyperlinkListener(new MyHyperLinkControls());
                                        } else {
                                            txtdisplay.setContentType("text/html");
                                            String s = "<br><p>" + user + ": " + message + "<br>" + timesend + "</p>";
                                            HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), s);
                                            String t = txtdisplay.getText().trim();
                                            txtdisplay.setText(t);
                                        }
                                        size--;
                                        if (size == 0) {
                                            break;
                                        }
                                    }
                                }
                                break;
                            case "kill_thread":
                                return;
                            case "receive_file":
                                in = new DataInputStream(client.getInputStream());
                                String username = in.readUTF();
                                String filename = in.readUTF();
                                String curdat = in.readUTF();
                                String urlString = in.readUTF();
                                URL url = new URL(urlString);
                                if (MainViews.currentChatWith.equals(username) && !MainViews.currentChatWith.equals("None")) {
                                    txtdisplay.setContentType("text/html");
                                    String sendFile = "\n" + username + ": <a href='" + url + "'>" + filename + "</a>" + "\n" + curdat + "\n";
                                    HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                    txtdisplay.setForeground(Color.blue);
                                    txtdisplay.addHyperlinkListener(new MyHyperLinkControls());
                                } else {
                                    thongBao.append(in.readUTF() + "\n");
                                }
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
