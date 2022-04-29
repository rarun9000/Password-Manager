
import java.util.ArrayList;

import IOClasses.ReadVerifyUserCredentials;
import Management.PasswordManagement;
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
        PasswordManagement m = new PasswordManagement();
        m.deleteMultipleAccount(3);
    }
    
}
