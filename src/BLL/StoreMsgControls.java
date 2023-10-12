/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.storeMsgModels;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class StoreMsgControls {

    public String checkChat(String username1, String username2) throws ClassNotFoundException, SQLException {
        storeMsgModels msg = new storeMsgModels();
        ResultSet check = msg.checkChat(username1, username2);
        String IDCHAT = "";
        if (check.next()) {
            IDCHAT = check.getString("IDCHAT");
        }
        return IDCHAT;
    }
    public int addNewChat(String username1, String usernam2) throws ClassNotFoundException, SQLException {
        storeMsgModels msg = new storeMsgModels();
        int check = msg.storeNewChat(username1, usernam2);
        if (check == 1) {
            return 1;
        }
        return 0;
    }
    public int storeMess(String IDCHAT, String username, String msg, String timesend) throws ClassNotFoundException, SQLException {
        storeMsgModels mess = new storeMsgModels();
        int check = mess.storeMsg(IDCHAT, username, msg, timesend);
        if (check == 1) return 1;
        return 0;
    }
    public ResultSet takeOldMessage(String IDCHAT) throws ClassNotFoundException, SQLException{
        storeMsgModels msg = new storeMsgModels();
        ResultSet res = msg.sendOldMessage(IDCHAT);
        return res;
    }
    public int storeMsgGroup(String IDCHAT, String username, String msg, String timesned) throws ClassNotFoundException, SQLException{
        storeMsgModels smm = new storeMsgModels();
        return smm.storeMsgGroup(IDCHAT, username, msg, timesned);
    } 
}
