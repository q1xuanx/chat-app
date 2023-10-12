/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class FriendModels {
    public int AddFriend(String username1, String username2) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec addFriend '"+username1+"', '"+username2+"'";
        int res = db.UpdateTable(q);
        return res;
    }
    public ResultSet displayFriend(String username1) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec displayuser '"+username1+"'";
        ResultSet res = db.ExQuery(q);
        return res;
    }
    public ResultSet checkFriendOrNot(String username1, String username2) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec checkFriendorNot '"+username1+"', '"+username2+"'";
        ResultSet res = db.ExQuery(q);
        return res;
    }
}
