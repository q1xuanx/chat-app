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
public class BlockModels {
    public int blockUser(String username1, String username2) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        ResultSet res = checkBlockOrNot(username1,username2);
        if (res.next()){
            return 3;
        }
        String q = "exec AddUserToBlock '"+username1+"','"+username2+"'";
        int block = db.UpdateTable(q);
        return block;
    }
    public ResultSet checkBlockOrNot(String username1, String username2) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec BlockorNote '"+username1+"', '"+username2+"'";
        ResultSet res = db.ExQuery(q);
        return res;
    }
    public int blockGroup(String username, String ID) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec userBlockGroup '"+username+"','"+ID+"'";
        return db.UpdateTable(q);
    }
}
