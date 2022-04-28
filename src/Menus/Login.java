package Menus;

import IOClasses.MenuHandler;
import IOClasses.scanner;
import Database.*;
import Users.UsersObject;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rarun
 */
public class Login {

    public Login() {
        readPrintProcessForm();
    }

    public Login(String st) {
    }
    LoginQueries lq;
    public String readVerifyUsername() {
        Scanner sc = scanner.scan;
        MenuHandler m = new MenuHandler();
        String user_id = "";
        while (true) {
            System.out.println("Enter UserId: ");            
            user_id = sc.nextLine();
            if (user_id.matches("^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")==false) {
                System.out.println("Invalid user id."+user_id);
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            lq = new LoginQueries(user_id);

            if (!lq.isValidUser()) {
                System.out.println("User Not Found.");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }

            break;
        }

        return user_id;
    }

    public String readVerifyPassword(String user_id) {
        Scanner sc =   scanner.scan;
        MenuHandler m = new MenuHandler();
        String hashed_password = lq.getHashPass();
        String master_password = "";
        while (true) {
            System.out.println("Enter Password: ");
            master_password = sc.nextLine();
            if(!master_password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$])[A-Za-z\\d@$#]{8,20}$")){
                System.out.println("Invalid password.");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            String master_password_hash = master_password.hashCode() + "";
            if (master_password_hash.equals(hashed_password) == false) {
                System.out.println("Invalid password.");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            break;
        }
        return master_password;
    }

    public final void readPrintProcessForm() {
        try {
            String username = readVerifyUsername();
            String master_password = readVerifyPassword(username);

            UserManagementQueries userQueries = new UserManagementQueries();

            String organization = userQueries.getOrganizationIdOfUser(username);
            String lastlogin = userQueries.getLastLoginOfUser(username);
            String usertype = userQueries.getUserType(username);

            UsersObject user = UsersObject.getInstance(new String[]{username, master_password, usertype, lastlogin, organization});

            Menu m = new Menu();
            m.printMainMenu();
            user.clear();
        } catch (RuntimeException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
