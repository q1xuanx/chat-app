/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import Controls.ServerControls;
import java.io.IOException;

/**
 *
 * @author Admin
 */
public class ServerView {

    static final int port = 7777;

    public static void main(String args[]) throws IOException {
        ServerControls sv = new ServerControls();
        System.out.println("Server đã sẵn sàng !");
        sv.acceptClient();
        while(true){
            if (sv.checkClient(sv.client)){
                System.out.println("Client đã thoát !");
                break;
            }
            sv.receiveMessage();
        }
        sv.sv.close();
    }
}
