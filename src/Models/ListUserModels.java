/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import com.sun.org.apache.xpath.internal.operations.Number;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.*;
/**
 *
 * @author Admin
 */
public class ListUserModels {
    public int totalUser() throws ClassNotFoundException, SQLException{
        int num = 0;
        DBAccess db = new DBAccess();
        String s = "DECLARE @numberuser int;exec listUser @numberuser = @numberuser output;select @numberuser as [Number User];";
        ResultSet rs = db.ExQuery(s);
        if (rs.next()){
            num = rs.getInt("Number User");
        }
        return num;
    }
}
