/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.RegisterModels;
import java.sql.SQLException;
import java.text.ParseException;

/**
 *
 * @author Admin
 */
public class RegisterControls {
    public int SignUp(String username, String password, String hoTen, String gioiTinh, String ngaysinh) throws ClassNotFoundException, SQLException, ParseException{
        RegisterModels rm = new RegisterModels();
        int signup = rm.registerConnect(username, password, hoTen, gioiTinh, ngaysinh);
        if (signup == 1) return 1;
        return 0;
    }
}
