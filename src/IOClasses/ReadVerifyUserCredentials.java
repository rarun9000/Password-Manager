/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IOClasses;

import Database.LoginQueries;
import Database.RegistrationQueries;
import Users.UsersObject;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author rarun
 */
public class ReadVerifyUserCredentials {

    Scanner sc = scanner.scan;
    UsersObject uo = UsersObject.getInstance();
    MenuHandler m = new MenuHandler();

    public ArrayList<String> getCredentials() {
        ArrayList<String> result = new ArrayList<>();
        result.add(readVerifyUsername());
        result.add(readVerifyPassword());
        return result;
    }

    public String readVerifyUsername() {
        LoginQueries lq = new LoginQueries();
        sc = new Scanner(System.in);
        boolean first = true;
        System.out.println();
        String uname = "";
        while (true) {
            System.out.println("Enter the username: ");
            if(first){
                first = false;
                System.out.println("\nNote: Username must be atlease 4 characters long."
                        + "\nUsername can only contain alphanumeric characters.\nUsername should not contain _ or . at the beginning or at the end.\n");
            }
            uname = sc.nextLine();
            if (uname.trim().isEmpty()) {
                System.out.println("Username cannot be empty!");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (!uname.matches("^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
                System.out.println("Invalid username!");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }            

            if (lq.isValidUserId(uname)) {
                System.out.println("Username already Exists!");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return uname;
        }
    }

    public String readVerifyPassword() {
        sc = new Scanner(System.in);
        System.out.println();
        boolean first = true;
        String pass = null, conf_pass = null;
        while (true) {
            if (pass == null) {
                System.out.println("Enter the password:  ");
                if (first) {
                    first = false;
                    System.out.println("\nNote: Password Must contain at least one digit\n"
                            + "Must contain at least one of the following special characters @, #, $\n"
                            + "The length should be between 8 to 20 characters.\n");
                }
                pass = sc.nextLine();
                pass = pass.trim();
            }
            if(pass.length() == 0){
                pass  = null;
                System.out.println("Password cannot be empty!");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (conf_pass == null && strongPasswordCheck(pass) == -1) {
                System.out.println("Weak Password detected.");
                m.doYouWantToReturnToPreviousMenu();
                pass = null;
                continue;
            }

            System.out.println("Confirm the password\t:   ");
            conf_pass = sc.next();

            if (!(pass.equals(conf_pass))) {
                System.out.println("Confirm password doesn't match");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return pass;
        }
    }

    public String readVerifyUserType(int type) {
        ArrayList<String> types = new ArrayList<>();
        types.add("user");
        types.add("admin");
        if (type == 3) {
            types.add("superadmin");
        }
        int choice = m.menuPrinterAndSelectionReturner(" new user's role", types, true);

        String new_user_type = "user";
        if (choice == 2) {
            new_user_type = "admin";
        }
        if (choice == 3) {
            new_user_type = "superadmin";
        }
        return new_user_type;
    }

    public String readVerifyOrganization() {
        sc = new Scanner(System.in);
        RegistrationQueries rq = new RegistrationQueries();
        while (true) {
            System.out.println("Enter Organization Name: ");
            String org = sc.nextLine();
            if (org.trim().isEmpty()) {
                System.out.println("Organization Name cannot be empty");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (org.matches("^[a-zA-Z0-9][a-zA-Z0-9\\.\\'\\-#&\\s]*$") == false) {
                System.out.println("Enter a valid Organization Name");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (org.length() < 4) {
                System.out.println("Organization name is too short");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }

            if (rq.isValidOrganization(org)) {
                System.out.println("Organization with same name already exists.");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return org;
        }
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static boolean isStringOnlyADigit(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[0-9]*$")));
    }

    public static int strongPasswordCheck(String password) {
        if (password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$])[A-Za-z\\d@$#]{8,20}$")){
            return 1;
        }
        return -1;
    }
}
