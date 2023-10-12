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
}
