/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.ListUserModels;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class UserControls {
    public ResultSet getAllUser() throws ClassNotFoundException, SQLException{
        ListUserModels lum = new ListUserModels();
        ResultSet res = lum.getAllUser();
        return res;
    }
    public void rmvFriend(String username1, String username2) throws ClassNotFoundException, SQLException{
        ListUserModels lum  = new ListUserModels();
        int rmv1 = lum.RmvFriend(username1, username2);
        int rmv2 = lum.RmvFriend(username2, username1);
        if (rmv1 == 1 && rmv2 == 1){
            System.out.println("[LOG] Đã remove bạn của " + username1 +" và " + username2);
        }
    }
    public int changePass(String username, String pass) throws ClassNotFoundException, SQLException{
        ListUserModels lms = new ListUserModels();
        return lms.changePass(username, pass);
    }
    public int isAdmin(String username) throws ClassNotFoundException, SQLException{
        ListUserModels lms = new ListUserModels();
        ResultSet res = lms.isAdmin(username);
        if (res.next()){
            return 1;
        }
        return 0;
    }
    public ResultSet getUserBan() throws ClassNotFoundException, SQLException{
        ListUserModels lms = new ListUserModels();
        return lms.displayBanUser();
    }
    public int UnBan(String username) throws ClassNotFoundException, SQLException{
        ListUserModels lms = new ListUserModels();
        return lms.UnBan(username);
    }
}
