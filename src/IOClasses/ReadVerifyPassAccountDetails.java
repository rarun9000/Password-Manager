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
public class ReadVerifyPassAccountDetails {

    MenuHandler menuH = new MenuHandler();
    Scanner scan = scanner.scan;
    PasswordManagementQueries Db = new PasswordManagementQueries();

    public String readUsername() {
        //account username
        scan = new Scanner(System.in);
        String username = "";
        while (true) {
            System.out.println("Enter the username\t:   ");
            username = scan.nextLine();
            username = username.trim();
            
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return username;
        }
    }

    public String readVerifyAccountName() {
        scan = new Scanner(System.in);
        String account_name = "";
        boolean first = true;
        while (true) {
            System.out.println("Enter Account Name: ");
            if(first){
                first  = false;
                System.out.println("Note: Account Name must have atleast 2 characters.\nAccount Name must contain only alphanumeric characters.\n");
            }
            account_name = scan.nextLine();
            account_name = account_name.trim();
//          
            if (account_name.matches("^[a-zA-Z0-9][a-zA-Z0-9\\.\\s]*$") == false) {
                System.out.println("Enter a valid Account Name Name");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (account_name.length() < 2) {
                System.out.println("Account Name is too short!");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (Db.checkIfAlreadyExists(account_name)) {
                System.out.println("Account with the same name already exists!");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return account_name;
        }
    }

    //Password of the Account which is to be stored in the app
    public String readPassword() {
        String pass = null;
        System.out.println("Enter the password:   ");
        pass = scan.nextLine();
        return pass.trim();
    }

    public String readUrl() {
        scan = new Scanner(System.in);
        System.out.println("Enter URL(press enter to skip) :  ");
        String url = scan.nextLine();
        return url.trim();
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static boolean isStringOnlyADigit(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[0-9]*$")));
    }
}
