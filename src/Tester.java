
import Menus.Menu;
import Users.UsersObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rarun
 */
public class Tester {
    public static void main(String[] args){
        UsersObject.getInstance(new String[]{"arun","1@ssword","superadmin",null,"zoho"});
        Menu m = new Menu();
        m.printMainMenu();       
    }
    
}
