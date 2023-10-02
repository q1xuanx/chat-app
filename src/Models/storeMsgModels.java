/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Admin
 */
public class storeMsgModels {

    DBAccess db;

    public ResultSet checkChat(String username1, String username2) throws ClassNotFoundException, SQLException {
        db = new DBAccess();
        String q = "exec checkMsg '" + username1 + "', '" + username2 + "'";
        ResultSet res = db.ExQuery(q);
        return res;
    }

    public int storeNewChat(String username1, String username2) throws ClassNotFoundException, SQLException {
        db = new DBAccess();
        String q = "exec addNewChat '" + username1 + "','" + username2 + "'";
        int exec = db.UpdateTable(q);
        if (exec == 1) {
            return 1;
        }
        return 0;
    }

    public int storeMsg(String idchat, String username, String msg, String timesend) throws ClassNotFoundException, SQLException {
        db = new DBAccess();
        try {
            String q = "exec storeMsg '" + idchat + "','" + username + "', '" + msg + "','" + timesend + "'";
            int exec = db.UpdateTable(q);
            if (exec == 1) return 1;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    public ResultSet sendOldMessage (String idChat) throws ClassNotFoundException, SQLException{
        db = new DBAccess();
        String q = "exec sendOldChat '"+idChat+"'";
        ResultSet res = db.ExQuery(q);
        return res;
    }
}
