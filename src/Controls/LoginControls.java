/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controls;

import Models.LoginModels;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class LoginControls {
    public boolean checkLogin(String username, String password) throws ClassNotFoundException, SQLException{
        LoginModels lm = new LoginModels();
        if(lm.loginToChat(username,password)){
            return true;
        }else {
            return false;
        }
    }
}
