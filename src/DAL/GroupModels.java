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
public class GroupModels {
    public int CreateGroup(String NameOfGroup) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec createGroup '"+NameOfGroup+"'";
        return db.UpdateTable(q);
    }
    public int addMemeber(String username, String idgroup) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec insertMemberToGroup '"+username+"','"+idgroup+"'";
        return db.UpdateTable(q);
    }
    public ResultSet getID(String name) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec getID '"+name+"'";
        return db.ExQuery(q);
    }
    public ResultSet getAllGroup() throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String s = "exec getAllGroup";
        return db.ExQuery(s);
    }
    public ResultSet checkJoinOrNot(String name, String IDChat) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String s = "exec checkMemberOrNo '"+name+"','"+IDChat+"'";
        return db.ExQuery(s);
    }
    public int deleteUserInGroup(String name, String IDGroup) throws SQLException, ClassNotFoundException{
        DBAccess db = new DBAccess();
        String s = "exec deleteUserInGroup '"+name+"', '"+IDGroup+"'";
        return db.UpdateTable(s);
    }
    public ResultSet checkUserBlockGroup(String name, String ID) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String s = "exec checkUserBlockGroupOrNot '"+name+"','"+ID+"'";
        return db.ExQuery(s);
    }
    public ResultSet getMember(String id) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String s = "exec getMember '"+id+"'";
        return db.ExQuery(s);
    }
    public ResultSet getOldMsg(String id) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String s = "exec getOldMsgGroup '"+id+"'";
        return db.ExQuery(s);
    }
}
