/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class RegisterModels {

    public int registerConnect(String username, String password, String hoTen, String gioiTinh, String ngaySinh) throws ClassNotFoundException, SQLException, ParseException {
        DBAccess db = new DBAccess();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(ngaySinh);
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        System.out.println(sqlDate);
        int gioitinh = 0;
        if (gioiTinh.equals("Nam")) {
            gioitinh = 1;
        }
        String sp = "exec Register '" + username + "', '" + password + "', '" + hoTen + "', '" + gioitinh + "', '" + sqlDate + "'";
        int rs = db.UpdateTable(sp);
        if (rs == 1) {
            return 1;
        }
        return 0;
    }
}
