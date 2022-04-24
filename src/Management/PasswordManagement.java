/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management;

import Database.PasswordManagementQueries;
import Database.UserManagementQueries;
import Users.UsersObject;
import IOClasses.*;
import Menus.Login;
import java.util.*;

/**
 *
 * @author rarun
 */
public class PasswordManagement {

    /*
            ----Admin & Super Admin options---
            1. Add Password Account
            2. Modify Password Account
            3. Delete Password Account
            4. List Passwords Shared By Me
            5. List Passwords Shared To Me
            ----------User only options-------
            6. List All Passwords.
            7. Return To Main Menu
     */
    UsersObject uo = UsersObject.getInstance();
    String current_user = uo.getUserId();
    MenuHandler menuH = new MenuHandler();
    ReadVerifyPassAccountDetails account = new ReadVerifyPassAccountDetails();
    PasswordManagementQueries Db = new PasswordManagementQueries();

    public String uniqueIDGenerator() {
        UUID id = UUID.randomUUID();
        return id.toString();
    }

    public void addPasswordAccount() {
        try {
            String res_id = uniqueIDGenerator();
            String account_name = account.readVerifyAccountName();
            String url = account.readUrl();
            String username = account.readUsername();
            String password = account.readPassword();
            Db.addPassword(account_name, username, url, password, res_id);
            System.out.println("Account Added Successfully");
        } catch (RuntimeException e) {
        }
    }

    public void listAllPasswords() {
        //retrieve account passwords created by the current user
        // ListAllPasswords -> Accounts Created by me  + Accounts shared to me

        ArrayList<String[]> all_accounts = new ArrayList<>();
        //name, url, username, password        

        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        all_accounts.addAll(accounts_created_by_me);

        ArrayList<String[]> accounts_shared_to_me = Db.getSharedToMeAccountDetails();
        all_accounts.addAll(accounts_shared_to_me);

        this.printSelectViewAccountDetails(all_accounts, false);
    }

    public void listPasswordsCreatedByMe() {
        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        this.printSelectViewAccountDetails(accounts_created_by_me, false);
    }

    public void listPasswordsSharedToMe() {
        ArrayList<String[]> accounts_shared_to_me = Db.getSharedToMeAccountDetails();
        this.printSelectViewAccountDetails(accounts_shared_to_me, true);
    }

    public void listPasswordsSharedByMe() {
        ArrayList<String[]> accounts_shared_by_me = Db.getSharedByMeAccountDetails();
//name,url,username,password,s.res_id,owner
        if (accounts_shared_by_me.isEmpty()) {
            System.out.println("******No Account Passwords Found******");
            return;
        }
        String header = this.getAccountDetailsPrinterHeader(false);
        String[] accounts = this.addOnlyAccountDetails(accounts_shared_by_me, false);
        System.out.println("Select the account name to view advance menu");
        try {
            int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);
            while (true) {
                if (choice - 1 == accounts_shared_by_me.size()) {
                    return;
                }
                listProcessAdvancedMenu(accounts_shared_by_me, choice);
                if (!menuH.doYouWantTo("select any other account")) {
                    return;
                }
                accounts_shared_by_me = Db.getSharedByMeAccountDetails();
                accounts = this.addOnlyAccountDetails(accounts_shared_by_me, false);
                if (accounts_shared_by_me.isEmpty()) {
                    System.out.println("******No Account Passwords Found******");
                    return;
                }
                choice = menuH.menuPrinterAndSelectionReturner(accounts, false);
            }
        } catch (RuntimeException e) {
        }
    }

    public void listProcessAdvancedMenu(ArrayList<String[]> accounts, int choice) {
        try {
            MenuHandler mh = new MenuHandler();

            String[] menu = new String[]{"View Password", "View list of sharee", "Return to Previous Menu"};
            int ch = mh.menuPrinterAndSelectionReturner(menu, true);
            while (true) {

                if (ch == 3) {
                    return;
                }
                if (ch == 2) {
                    while (true) {
                        ArrayList<String> sharee = Db.listOfShareeOfAnAccount(accounts.get(choice - 1)[4]);
                        if (sharee.isEmpty()) {
                            System.out.println("Account Not shared with anyone");
                            return;
                        }
                        //mh.printList(mh.arrayListToStringArrayConverter(sharee));
                        sharee.add("Skip");
                        System.out.println("Select sharee to revoke access for '" + accounts.get(choice - 1)[0].toUpperCase() + "' or press " + sharee.size() + " to skip");
                        int choice2 = mh.menuPrinterAndSelectionReturner(mh.arrayListToStringArrayConverter(sharee), true);
                        if (!sharee.get(choice2 - 1).equals("Skip")) {
                            String sharee_id = sharee.get(choice2 - 1);
                            String res_id = accounts.get(choice - 1)[4];
                            Db.revokeAccessOfAccountToUser(res_id, sharee_id);
                            System.out.println("Access to " + accounts.get(choice - 1)[0] + " revoked from user" + sharee_id);

                            if (mh.doYouWantTo("revoke access to another sharee")) {
                                continue;
                            }
                        }
                        break;
                    }
                }
                if (ch == 1) {
                    System.out.println("Username : " + accounts.get(choice - 1)[2] + "\tPassword : " + accounts.get(choice - 1)[3]);
                }

                if (!mh.doYouWantTo("select any other option")) {
                    return;
                }

                ch = menuH.menuPrinterAndSelectionReturner(menu, false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void printSelectViewAccountDetails(ArrayList<String[]> all_accounts, boolean withsharer) {
        if (all_accounts.isEmpty()) {
            System.out.println("******No Account Passwords Found******");
            return;
        }
        MenuHandler menuH = new MenuHandler();
        String header = this.getAccountDetailsPrinterHeader(withsharer);
        String[] accounts = addOnlyAccountDetails(all_accounts, withsharer);
        menuH.maxLengthInList(accounts);

        String function = "Select the account name to view the password";

        System.out.println(function);
        int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);

        while (true) {

            if (choice - 1 == all_accounts.size()) {
                return;
            }
            //all or sharedtome and created by me -> same functio
            //sharedbyme -> list of users i shared with and option to revoke access to a user
            System.out.println("Username : " + all_accounts.get(choice - 1)[2] + "\tPassword : " + all_accounts.get(choice - 1)[3]);
            if (!menuH.doYouWantTo("view another account password")) {
                break;
            }

            choice = menuH.menuPrinterAndSelectionReturner(accounts, false);
        }
    }

    private String[] addOnlyAccountDetails(ArrayList<String[]> account_details, boolean withsharer) {
        String[] accounts = new String[account_details.size() + 1];
        int indx = 0;
        for (; indx < account_details.size(); indx++) {
            String name = account_details.get(indx)[0];
            String url = account_details.get(indx)[1].trim();
            String username = account_details.get(indx)[2];
            if (!withsharer) {
                accounts[indx] = accountDetailsFormatter(name, url, username);
            } else {
                String sharer = account_details.get(indx)[5];
                accounts[indx] = accountDetailsFormatter(name, url, username, sharer);
            }
        }
        accounts[indx] = formatString("Return to previous menu");
        return accounts;
    }

    private String accountDetailsFormatter(String name, String url, String username) {
        name = formatString(name);
        if (url.length() == 0) {
            url = "Url Not Specified";
        }
        url = formatString(url);
        username = formatString(username);
        String concat = name + "|" + url + "|" + username + "|" + formatString("**********");
        return concat;
    }

    private String accountDetailsFormatter(String name, String url, String username, String sharer) {
        name = formatString(name);
        if (url.length() == 0) {
            url = "Url Not Specified";
        }
        url = formatString(url);
        username = formatString(username);
        String concat = name + "|" + url + "|" + username + "|" + formatString("**********") + "|" + formatString(sharer);
        return concat;
    }

    private String getAccountDetailsPrinterHeader(boolean withSharer) {
        if (!withSharer) {
            return "   " + formatString("Account Name") + "|" + formatString("URL") + "|" + formatString("Username") + "|" + formatString("Password");
        }
        return "   " + formatString("Account Name") + "|" + formatString("URL") + "|" + formatString("Username") + "|" + formatString("Password") + "|" + formatString("Shared By");
    }

    public void modifyAccount() {
        //update details of password account
        while (true) {
            ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
            //name,url,username,password,res_id
            MenuHandler menuH = new MenuHandler();
            if (accounts_created_by_me.isEmpty()) {
                System.out.println("******No Account Passwords Found******");
                return;
            }
            try {
                String header = getAccountDetailsPrinterHeader(false);
                String[] accounts = addOnlyAccountDetails(accounts_created_by_me, false);
                menuH.maxLengthInList(accounts);
                System.out.println("Select the account name which has to be updated");
                int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);

                if (choice == accounts.length) {
                    return;
                }
                String currentAccountName = accounts_created_by_me.get(choice - 1)[0];
                String res_id = accounts_created_by_me.get(choice - 1)[4];

                ReadAccountDetailsForUpdate readDetails = new ReadAccountDetailsForUpdate();
                //get update
                String newAccountName = readDetails.readNewAccountName(currentAccountName);
                String newUrl = readDetails.readNewUrl();
                String newUsername = readDetails.readNewUsername();
                String newPassword = readDetails.readNewPassword();
                //check if changed

                boolean checkIfNameChanged = !(newAccountName == null); // TRUE -> YES
                boolean checkIfUrlChanged = !(newUrl == null);
                boolean checkIfUsernameChanged = !(newUsername == null);
                boolean checkIfPasswordChanged = !(newPassword == null);

                if (checkIfNameChanged || checkIfUrlChanged || checkIfUsernameChanged || checkIfPasswordChanged) {
                    Db.updateAccountDetails(newAccountName, newUrl, newUsername, newPassword, res_id);
                    System.out.println("Account Updated successfully.");

                } else {
                    System.out.println("No new changes detected.");
                }

            } catch (RuntimeException e) {
            }
            if (!menuH.doYouWantTo("update another account")) {
                break;
            }
        }
    }

    public void sharePassword() {
        //select the account whose password should be shared
        //select the user with whom the password should be shared

        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        if (accounts_created_by_me.isEmpty()) {
            System.out.println("******No Account Passwords Found******");
            return;
        }

        System.out.println("Select the account which you want to share.");
        String header = "   " + formatString("Account Name") + "|" + formatString("URL") + "|" + formatString("Username") + "|" + formatString("Password");
        String[] accounts = addOnlyAccountDetails(accounts_created_by_me, false);
        int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);

        if (choice - 1 == accounts_created_by_me.size()) {
            return;
        }

        //get sharee username -- use readUser function from login
        try {
            Login login = new Login("ResuseFunction");
            String sharee = "";
            while (true) {
                sharee = login.readVerifyUsername();
                if (sharee.equals(current_user)) {
                    System.out.println("Cannot share account with the current user.");
                    menuH.doYouWantToReturnToPreviousMenu();
                    continue;
                }
                //check if both sharee and sharer belong to same organization
                //use getOrganization methof from usermanagementqueries class
                UserManagementQueries umq = new UserManagementQueries();
                String sharee_org = umq.getOrganizationOfUser(sharee);
                if (uo.getOrganization().equals(sharee_org) == false) {
                    System.out.println("User Not Found.");
                    menuH.doYouWantToReturnToPreviousMenu();
                    continue;
                }
                break;
            }

            String res_id = accounts_created_by_me.get(choice - 1)[4];
            //check if already shared
            if (Db.checkIfAlreadyShared(sharee, res_id)) {
                System.out.println("Account already shared with " + sharee);
                return;
            }

            Db.shareAccount(sharee, res_id);
            System.out.println("Account successfully shared");
        } catch (RuntimeException e) {
        }
    }

    public void deleteAccount() {
        ArrayList<String[]> all_accounts = Db.getAccountDetailsCreatedByMe();
        //first revoke access to this account for all other users;
        if (all_accounts.isEmpty()) {
            System.out.println("**No Account Passwords Found**");
        }
        String[] accounts = this.addOnlyAccountDetails(all_accounts, false);
        //name,url,username,password,res_id
        String header = this.getAccountDetailsPrinterHeader(false);
        MenuHandler menuH = new MenuHandler();
        System.out.println("Select the account which you want to deleted");
        try {
            int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);
            if (choice - 1 == all_accounts.size()) {
                return;
            }
            String res_id = all_accounts.get(choice - 1)[4];
            //first revoke access to this account for all other users;
            //Db.revokeAccessOfThisAccountToAllOtherUsers(res_id);
            //now remove this account from accounts table
            Db.deleteAccount(res_id);
        } catch (RuntimeException e) {
        }
        System.out.println("Account deleted successfully");
    }

    private String formatString(String str) {
        int len = str.length();
        int space = 1;

        //Leading space Generator
        int middle = (30 - len) / 2;
        while (space++ < middle) {
            str = " " + str;
        }
        space += len;
        //Trailing space Generator
        while (space++ <= 30) {
            str = str + " ";
        }
        return str;
    }
}
