
import java.util.ArrayList;

import IOClasses.ReadVerifyUserCredentials;
import Management.UserManagement;
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
        ReadVerifyUserCredentials obj = new ReadVerifyUserCredentials();  
        try {
            ArrayList<String[]> users = obj.readVerifyMultipleUsername("invite", "invitee");
            System.out.println(new UserManagement().ArrayListToStringQuery(users));

        } catch (Exception e) {
            System.out.println("Error Thrown");
        }
    }
    
}
