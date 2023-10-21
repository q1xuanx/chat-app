/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import com.sun.org.apache.xpath.internal.operations.Number;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.*;
/**
 *
 * @author Admin
 */
public class ListUserModels {
    public int totalUser() throws ClassNotFoundException, SQLException{
        int num = 0;
        DBAccess db = new DBAccess();
        String s = "DECLARE @numberuser int;exec listUser @numberuser = @numberuser output;select @numberuser as [Number User];";
        ResultSet rs = db.ExQuery(s);
        if (rs.next()){
            num = rs.getInt("Number User");
        }
        return num;
    }
    public ResultSet getAllUser() throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec selectAllUser";
        ResultSet rs = db.ExQuery(q);
        return rs;
    }
    public int RmvFriend(String username1, String username2) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec rmvFriend '"+username1+"', '"+username2+"'";
        return db.UpdateTable(q);
    }
    public int changePass(String username, String pass) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec ChangPass '"+username+"', '"+pass+"'";
        return db.UpdateTable(q);
    }
    public ResultSet isAdmin(String username) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec isAdmin '"+username+"'";
        return db.ExQuery(q);
    }
    public ResultSet displayBanUser() throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec getUserBan";
        return db.ExQuery(q);
    }
    public int UnBan(String username) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec unBan '"+username+"'";
        return db.UpdateTable(q);
    }
}
