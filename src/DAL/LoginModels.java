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
public class LoginModels {
    public boolean loginToChat(String username, String password) throws ClassNotFoundException, SQLException{
        DBAccess db = new DBAccess();
        String sp = "exec userLogin '"+username+"', '"+password+"'";
        ResultSet res = db.ExQuery(sp);
        if (res.next()){
            return true;
        }
        return false;
    }
}
