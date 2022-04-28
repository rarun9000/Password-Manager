/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Menus;

import Database.RegistrationQueries;
import IOClasses.*;
import Users.UsersObject;
import java.util.ArrayList;

/**
 *
 * @author rarun
 */
public class Registration {

    public Registration() {
        processForm();
    }

    public final void processForm() {
        try {
            RegistrationQueries rq = new RegistrationQueries();
            ReadVerifyUserCredentials read = new ReadVerifyUserCredentials();

            String org_name = read.readVerifyOrganization();
            ArrayList<String> cred = read.getCredentials();
            String Id =  rq.addOrganizationAndUser(cred.get(0), cred.get(1), org_name);
            //after registration, move to dashborad
            String[] data = {cred.get(0), cred.get(1), "superadmin", null, Id};

            UsersObject.getInstance(data);
            Menu m = new Menu();
            m.printMainMenu();
        } catch (RuntimeException e) {
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
