/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IOClasses;

import Database.PasswordManagementQueries;
import java.util.Scanner;

/**
 *
 * @author rarun
 */
public class ReadAccountDetailsForUpdate {

    //read new account name
    //read new url
    //read new username
    //read new password
    MenuHandler menuH = new MenuHandler();
    PasswordManagementQueries Db = new PasswordManagementQueries();
    Scanner scan;

    public String readNewAccountName(String currentName) {
        scan = scanner.scan;
        String newAccountName = "";
        while (true) {
            System.out.println("Enter New Account Name (Press Enter to Skip) : ");
            newAccountName = scan.nextLine();
            newAccountName = newAccountName.trim();
            if (newAccountName.length() == 0) {
                return null;
            }
            if (newAccountName.matches("^[a-zA-Z0-9][a-zA-Z0-9\\.\\s]*$") == false) {
                System.out.println("Enter a valid Account Name Name");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (newAccountName.length() < 2) {
                System.out.println("Account Name is too short!");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (newAccountName.equals(currentName)) {
                System.out.println("New name cannot be the same as current name!");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (Db.checkIfAlreadyExists(newAccountName)) {
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            break;
        }
        return newAccountName;
    }

    public String readNewUsername() {
        scan = new Scanner(System.in);
        String newUsername = "";

        System.out.println("Enter New Username (Press Enter to Skip) :   ");
        newUsername = scan.nextLine();
        newUsername = newUsername.trim();

        return newUsername.isEmpty() ? null : newUsername;

    }

//    public String readNewUrl(String current_url){
//        
//    }
//    
    public String readNewPassword() {
        String pass = null;
        System.out.println("Enter the password (press enter to skip):   ");
        pass = scan.nextLine();
        pass = pass.trim();
        return pass.isEmpty() ? null : pass;
    }

    public String readNewUrl() {
        scan = new Scanner(System.in);
        System.out.println("Enter URL(press enter to skip) :  ");
        String url = scan.nextLine();
        url = url.trim();
        return url.isEmpty() ? null : url;
    }
}
