/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.BanUserModels;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class BanControls {
    public void BanUser(String username) throws ClassNotFoundException, SQLException{
        BanUserModels bm = new BanUserModels();
        ResultSet res = bm.FindNameToBan(username);
        if (res.next()){
        int baned = bm.BanUser(username);
        if (baned == 1){
            System.out.println("Đã ban thành công");
        }else {
            System.out.println("Có lỗi");
        }
        }else {
            System.out.println("Không tìm thấy user");
        }
    }
}
