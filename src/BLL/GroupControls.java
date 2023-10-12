/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.GroupModels;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class GroupControls {
    public int createGroup(String name) throws ClassNotFoundException, SQLException{
        GroupModels gm = new GroupModels();
        return gm.CreateGroup(name);
    }
    public int addMember(String username, String idgroup) throws ClassNotFoundException, SQLException{
        GroupModels gm = new GroupModels();
        return gm.addMemeber(username, idgroup);
    }
    public String getID(String name) throws ClassNotFoundException, SQLException{
        GroupModels gm = new GroupModels();
        ResultSet rs = gm.getID(name);
        if (rs.next()){
            return rs.getString(1);
        }
        return null;
    }
    public ResultSet getAllGroup() throws ClassNotFoundException, SQLException{
        GroupModels gm = new GroupModels();
        return gm.getAllGroup();
    }
    public ResultSet checkJoinOrNot(String name, String IDChat) throws ClassNotFoundException, SQLException{
        GroupModels gm = new GroupModels();
        return gm.checkJoinOrNot(name, IDChat);
    }
    public int deleteUserInGroup(String name, String IDGroup) throws SQLException, ClassNotFoundException{
        GroupModels gm = new GroupModels();
        return gm.deleteUserInGroup(name, IDGroup);
    }
    public int checkUserBlockGroup (String name, String ID) throws ClassNotFoundException, SQLException, SQLException{
        GroupModels gm = new GroupModels();
        ResultSet res = gm.checkUserBlockGroup(name, ID);
        if (res.next()) return 1;
        return 0;
    }
    public ArrayList<String> getMember(String id) throws ClassNotFoundException, SQLException{
        ArrayList<String> arr = new ArrayList<String>();
        GroupModels gms = new GroupModels();
        ResultSet res = gms.getMember(id);
        while(res.next()){
            arr.add(res.getString(1));
        }
        return arr;
    }
    public ResultSet getOldMsg(String ID) throws ClassNotFoundException, SQLException{
        GroupModels gr = new GroupModels();
        ResultSet getOld = gr.getOldMsg(ID);
        return getOld;
    }
}
