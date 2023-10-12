/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.BlockModels;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class BlockControls {
    public int addUserToBlock(String username1, String username2) throws ClassNotFoundException, SQLException{
        BlockModels bm = new BlockModels();
        return bm.blockUser(username1, username2);
    }
    public ResultSet checkBlock(String username1, String username2) throws ClassNotFoundException, SQLException{
        BlockModels bm = new BlockModels();
        ResultSet rs = bm.checkBlockOrNot(username1, username2);
        return rs;
    }
    public int userBlockGroup (String username, String ID) throws ClassNotFoundException, SQLException{
        BlockModels bm = new BlockModels();
        return bm.blockGroup(username, ID);
    }
}
