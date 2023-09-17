/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import static jdk.nashorn.internal.objects.NativeFunction.call;

/**
 *
 * @author Admin
 */
public class DBAccess {
    private Connection con;
    private Statement stmt;
   
    public DBAccess() throws ClassNotFoundException, SQLException{
        try{
        DBConnection db = new DBConnection();
        con = db.getConnect();
        stmt = con.createStatement();
        }catch (ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, e.toString() ,"Fail",JOptionPane.ERROR_MESSAGE);
        }
    }
    public int UpdateTable(String s){
        try{
            stmt.executeUpdate(s);
            return 1;
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.toString() ,"Fail",JOptionPane.ERROR_MESSAGE);
            return 0;
   
        }
    }
    public ResultSet ExQuery(String s){
        try{
            ResultSet rs = stmt.executeQuery(s);
            return rs;
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,e.toString(),"Fail", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
