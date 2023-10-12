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
public class BanUserModels {
    public int BanUser(String username) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec BanUser '"+username+"'";
        return db.UpdateTable(q);
    }
    public ResultSet FindNameToBan(String username) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String q = "exec findExactName'"+username+"'";
        return db.ExQuery(q);
    }
}
