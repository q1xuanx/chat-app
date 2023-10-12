/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.DBAccess;
import DAL.FriendModels;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class FriendControls {
    public int addFriend(String username1, String username2) throws ClassNotFoundException, SQLException{
        FriendModels fm = new FriendModels();
        ResultSet res = fm.checkFriendOrNot(username1, username2);
        if(res.next()){
            return 3;
        }
        int add = fm.AddFriend(username1, username2);
        return add;
    }
    public ResultSet displayFriend(String username1) throws ClassNotFoundException, SQLException{
        FriendModels fm = new FriendModels();
        return fm.displayFriend(username1);
    }
    public int checkFriend(String username1, String username2) throws ClassNotFoundException, SQLException{
        FriendModels fm = new FriendModels();
        if (fm.checkFriendOrNot(username1, username2).next()){
            return 1;
        }
        return 0;
    }
}
