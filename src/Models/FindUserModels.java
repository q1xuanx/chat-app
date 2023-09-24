/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultListModel;

/**
 *
 * @author Admin
 */
public class FindUserModels {
    public DefaultListModel FindUser(String username) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec findUserLikeName '"+username+"'";
        ResultSet rs = db.ExQuery(q);
        DefaultListModel ls = new DefaultListModel();
        while(rs.next()){
            ls.addElement(rs.getString("username"));
        }
        return ls;
    }
}
