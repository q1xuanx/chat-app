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
                                    String s = userName + ": " + msg + "<br>" + curdate + "<br>";
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
                                            String sendFile = user + ": <a href='" + urls + "'>" + filesave.getName() + "</a>" + "<br>" + timesend + "<br>";
                                            HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                            txtdisplay.setForeground(Color.blue);
                                            txtdisplay.addHyperlinkListener(new MyHyperLinkControls());
                                        } else if (message.contains("[IMAGE]")) {
                                            txtdisplay.setContentType("text/html");
                                            String start = "[IMAGE]";
                                            String file = message.replace(start, "");
                                            StyledDocument doc = (StyledDocument) txtdisplay.getDocument();
                                            Style st = doc.addStyle("Image", null);
                                            StyleConstants.setIcon(st, new ImageIcon(file));
                                            doc.insertString(doc.getLength(), "\n", null);
                                            doc.insertString(doc.getLength(), user, null);
                                            doc.insertString(doc.getLength(), "Image", st);
                                            doc.insertString(doc.getLength(), "\n", null);
                                            doc.insertString(doc.getLength(), timesend, null);
                                            doc.insertString(doc.getLength(), "\n", null);
                                        } else {
                                            txtdisplay.setContentType("text/html");
                                            String s = user + ": " + message + "<br>" + timesend + "<br>";
                                            HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), s);
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
                                    String sendFile = "<br>" + username + ": <a href='" + url + "'>" + filename + "</a>" + "<br>" + curdat + "<br>";
                                    HTMLDocument doc = (HTMLDocument) txtdisplay.getDocument();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), sendFile);
                                    txtdisplay.setForeground(Color.blue);
                                    txtdisplay.addHyperlinkListener(new MyHyperLinkControls());
                                } else {
                                    thongBao.append(in.readUTF() + "\n");
                                }
                                break;
                            case "rei_pic":
                                String nameuser = in.readUTF();
                                String pathPic = in.readUTF();
                                String datesend = in.readUTF();
                                txtdisplay.setContentType("text/html");
                                StyledDocument doc = (StyledDocument) txtdisplay.getDocument();
                                Style style = doc.addStyle("Image", null);
                                StyleConstants.setIcon(style, new ImageIcon(pathPic));
                                try {
                                    doc.insertString(doc.getLength(), "\n", null);
                                    doc.insertString(doc.getLength(), nameuser, null);
                                    doc.insertString(doc.getLength(), "Image", style);
                                    doc.insertString(doc.getLength(), "\n", null);
                                    doc.insertString(doc.getLength(), datesend, null);
                                    doc.insertString(doc.getLength(), "\n", null);
                                } catch (BadLocationException ex) {
                                    Logger.getLogger(MainViews.class.getName()).log(Level.SEVERE, null, ex);
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
