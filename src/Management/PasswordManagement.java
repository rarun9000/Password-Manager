/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management;

import Database.PasswordManagementQueries;
import Database.UserManagementQueries;
import Encryption.*;
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
     * ----Admin & Super Admin options---
     * 1. Add Password Account
     * 2. Modify Password Account
     * 3. Delete Password Account
     * 4. List Passwords Shared By Me
     * 5. List Passwords Shared To Me
     * ----------User only options-------
     * 6. List All Passwords.
     * 7. Return To Main Menu
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
            String username = account.readUsername();
            String password = account.readPassword();
            String key = uniqueIDGenerator();
            // System.out.println(" Key: "+key);
            String encryptedPassword = AES.encrypt(password, key);
            String encryptedKey = RSAUtil.getEncryptedString(key, uo.getPublicKey());

            Db.addPassword(account_name, username, encryptedPassword, res_id, encryptedKey);
            System.out.println("Account Added Successfully");
        } catch (RuntimeException e) {
        }
    }

    public void listAllPasswords() {
        // retrieve account passwords created by the current user
        // ListAllPasswords -> Accounts Created by me + Accounts shared to me

        ArrayList<String[]> all_accounts = new ArrayList<>();

        // accountName,username,encryptedPassword,resId
        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        all_accounts.addAll(accounts_created_by_me);

        // accountName,username,encryptedPassword,resId,owner
        ArrayList<String[]> accounts_shared_to_me = Db.getSharedToMeAccountDetails();
        all_accounts.addAll(accounts_shared_to_me);

        this.printSelectViewAccountDetails(all_accounts, false);
    }

    public void listPasswordsCreatedByMe() {
        // accountName,username,encryptedPassword,resId,encryptedKey
        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        this.printSelectViewAccountDetails(accounts_created_by_me, false);
    }

    public void listPasswordsSharedToMe() {
        // accountName,username,encryptedPassword,resId,encryptedKey,owner
        ArrayList<String[]> accounts_shared_to_me = Db.getSharedToMeAccountDetails();
        this.printSelectViewAccountDetails(accounts_shared_to_me, false);
    }

    public void listPasswordsSharedByMe() {
        ArrayList<String[]> accounts_shared_by_me = Db.getSharedByMeAccountDetails();
        // accountName,username,encryptedPassword,resId,encryptedKey
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

            String[] menu = new String[] { "View Password", "View list of sharee", "Return to Previous Menu" };
            int ch = mh.menuPrinterAndSelectionReturner(menu, true);
            while (true) {

                if (ch == 3) {
                    return;
                }
                if (ch == 2) {
                    while (true) {
                        ArrayList<String> sharee = Db.listOfShareeOfAnAccount(accounts.get(choice - 1)[3]);
                        if (sharee.isEmpty()) {
                            System.out.println("Account Not shared with anyone");
                            return;
                        }
                        // mh.printList(mh.arrayListToStringArrayConverter(sharee));
                        sharee.add("Skip");
                        System.out.println(
                                "Select sharee to revoke access for '" + accounts.get(choice - 1)[0].toUpperCase()
                                        + "' or press " + sharee.size() + " to skip");
                        int choice2 = mh.menuPrinterAndSelectionReturner(mh.arrayListToStringArrayConverter(sharee),
                                true);
                        if (!sharee.get(choice2 - 1).equals("Skip")) {
                            String sharee_id = sharee.get(choice2 - 1);
                            String res_id = accounts.get(choice - 1)[3];
                            Db.revokeAccessOfAccountToUser(res_id, sharee_id);
                            System.out.println(
                                    "Access to " + accounts.get(choice - 1)[0] + " revoked from user" + sharee_id);

                            if (mh.doYouWantTo("revoke access to another sharee")) {
                                continue;
                            }
                        }
                        break;
                    }
                }
                if (ch == 1) {
                    String encryptedPassword = accounts.get(choice - 1)[2];
                    String encryptedKey = accounts.get(choice - 1)[4];

                    String decryptedKey = "";
                    try {
                        decryptedKey = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
                    } catch (Exception e) {
                    }

                    String decryptedPassword = AES.decrypt(encryptedPassword, decryptedKey);

                    System.out.println(
                            "Username : " + accounts.get(choice - 1)[1] + "\tPassword : " + decryptedPassword);
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
        // accountName,username,encryptedPassword,resId,encryptedKey

        if (all_accounts.isEmpty()) {
            System.out.println("******No Account Passwords Found******");
            return;
        }

        MenuHandler menuH = new MenuHandler();
        String header = this.getAccountDetailsPrinterHeader(withsharer);
        String[] accounts1 = addOnlyAccountDetails(all_accounts, withsharer);
        String[] accounts = new String[accounts1.length + 1];
        int i = 0;
        for (; i < accounts1.length; i++) {
            accounts[i] = accounts1[i];
        }
        accounts[i] = "Back to Previous Menu";
        menuH.maxLengthInList(accounts);

        String function = "Select the account name to view the password";

        System.out.println(function);
        int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);

        while (true) {

            if (choice - 1 == all_accounts.size()) {
                return;
            }
            // all or sharedtome and created by me -> same functio
            // sharedbyme -> list of users i shared with and option to revoke access to a
            // user
            String encryptedPassword = all_accounts.get(choice - 1)[2];
            String encryptedKey = all_accounts.get(choice - 1)[4];

            String decryptedKey = "";
            try {
                decryptedKey = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
            } catch (Exception e) {
            }

            String decryptedPassword = AES.decrypt(encryptedPassword, decryptedKey);

            System.out.println("Username : " + all_accounts.get(choice - 1)[1] + "\tPassword : " + decryptedPassword);
            if (!menuH.doYouWantTo("view another account password")) {
                break;
            }

            choice = menuH.menuPrinterAndSelectionReturner(accounts, false);
        }
    }

    private String[] addOnlyAccountDetails(ArrayList<String[]> account_details, boolean withsharer) {
        String[] accounts = new String[account_details.size()];
        // accountName,username,encryptedPassword,resId,encryptedKey
        int indx = 0;
        for (; indx < account_details.size(); indx++) {
            String name = account_details.get(indx)[0];
            String username = account_details.get(indx)[1];
            if (!withsharer) {
                accounts[indx] = accountDetailsFormatter(name, username);
            } else {
                String sharer = account_details.get(indx)[5];
                accounts[indx] = accountDetailsFormatter(name, username, sharer);
            }
        }
        // accounts[indx] = formatString("Return to previous menu");
        return accounts;
    }

    private String accountDetailsFormatter(String accountName, String username) {
        accountName = formatString(accountName);

        username = formatString(username);
        String concat = accountName + "|" + username + "|" + formatString("**********");
        return concat;
    }

    private String accountDetailsFormatter(String name, String username, String sharer) {
        name = formatString(name);

        username = formatString(username);
        String concat = name + "|" + username + "|" + formatString("**********") + "|" + formatString(sharer);
        return concat;
    }

    private String getAccountDetailsPrinterHeader(boolean withSharer) {
        if (!withSharer) {
            return "   " + formatString("Account Name") + "|" + formatString("Username") + "|"
                    + formatString("Password");
        }
        return "   " + formatString("Account Name") + "|" + formatString("Username") + "|" + formatString("Password")
                + "|" + formatString("Shared By");
    }

    public void modifyAccount() {
        // update details of password account
        while (true) {
            ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
            // name,url,username,password,res_id
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
                // get update
                String newAccountName = readDetails.readNewAccountName(currentAccountName);
                String newUsername = readDetails.readNewUsername();
                String newPassword = readDetails.readNewPassword();
                // check if changed

                boolean checkIfNameChanged = !(newAccountName == null); // TRUE -> YES
                boolean checkIfUsernameChanged = !(newUsername == null);
                boolean checkIfPasswordChanged = !(newPassword == null);

                if (checkIfPasswordChanged) {
                    String encryptedKey = accounts_created_by_me.get(choice - 1)[4];
                    String key = "";
                    try {
                        key = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
                    } catch (Exception e) {
                    }
                    newPassword = AES.encrypt(newPassword, key);
                }

                if (checkIfNameChanged || checkIfUsernameChanged || checkIfPasswordChanged) {
                    Db.updateAccountDetails(newAccountName, newUsername, newPassword, res_id);
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
        // select the account whose password should be shared
        // select the user with whom the password should be shared

        ArrayList<String[]> accounts_created_by_me = Db.getAccountDetailsCreatedByMe();
        if (accounts_created_by_me.isEmpty()) {
            System.out.println("******No Account Passwords Found******");
            return;
        }

        System.out.println("Select the account which you want to share.");
        String header = "   " + formatString("Account Name") + "|" + formatString("Username") + "|"
                + formatString("Password");
        String[] accounts1 = addOnlyAccountDetails(accounts_created_by_me, false);
        String[] accounts = new String[accounts1.length + 1];
        int i = 0;
        for (; i < accounts1.length; i++) {
            accounts[i] = accounts1[i];
        }
        accounts[i] = "Back to Previous Menu";
        int choice = menuH.menuPrinterAndSelectionReturner(header, accounts, true);

        if (choice - 1 == accounts_created_by_me.size()) {
            return;
        }

        // get sharee username -- use readUser function from login
        new UserManagement().listUsers();
        try {
            ArrayList<String> all_users = new UserManagementQueries().getUsernameList();
            // accountName,username,encryptedPassword,resId,encryptedKey
            ArrayList<Integer> user = selectMultipleUser(all_users.size()-1);
            String res_id = accounts_created_by_me.get(choice - 1)[3];
            String encryptedKey = accounts_created_by_me.get(choice - 1)[4];
            String decryptedKey = "";
            try {
                decryptedKey = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
            } catch (Exception e) {
                System.out.println("Sharing error: " + e);
            }

            for (int iterator : user) {
                String sharee = all_users.get(iterator - 1);
                if (Db.checkIfAlreadyShared(sharee, res_id) ||  sharee.equals(uo.getUserId())) {
                    continue;
                }
                String shareePublicKey = new UserManagementQueries().getPublicKey(sharee);
                String encryptedKeyForSharee = RSAUtil.getEncryptedString(decryptedKey, shareePublicKey);
                System.out.println("Shared Encrypted Key: " + encryptedKeyForSharee);
                Db.shareAccount(sharee, res_id, encryptedKeyForSharee);
            }
            // check if already shared

            System.out.println("Account successfully shared with selected user(s)");
        } catch (RuntimeException e) {
        }
    }

    public ArrayList<Integer> selectMultipleUser(int limit) {
        ArrayList<Integer> list = new ArrayList<>();
        HashSet<Integer> selected = new HashSet<>();
        int start = 1;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter User " + start + ": ");
            String inp = scanner.nextLine();

            if (inp.trim().isEmpty()) {
                if (list.isEmpty()) {
                    System.out.println("No User Selected");
                    menuH.doYouWantToReturnToPreviousMenu();
                    continue;
                }
                break;
            }
            int val = Integer.parseInt(inp);

            if (val <= 0 || val > limit) {
                System.out.println("Invalid Selection");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (selected.contains(val)) {
                System.out.println("User Already Selected");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            selected.add(val);
            list.add(val);
            start++;
            if (start == limit + 1) {
                System.out.println("All Users Are Selected");
                break;
            }
        }
        return list;
    }

    public void deleteAccount() {
        ArrayList<String[]> all_accounts = Db.getAccountDetailsCreatedByMe();
        if (all_accounts.isEmpty()) {
            System.out.println("**No Account To Delete**");
            return;
        }
        String[] accounts = this.addOnlyAccountDetails(all_accounts, false);
        // name,url,username,password,res_id
        MenuHandler menuH = new MenuHandler();
        menuH.max_length = Math.max(menuH.max_length, accounts[0].length());
        //menuH.printList(accounts);
        System.out.println(
                "Select the account(s) which you want to deleted. Leave the input empty to end the selection.");
        try {
            ArrayList<String> delete_accounts = new ArrayList<>();
            for (int i : deleteMultipleAccount(all_accounts.size())) {
                System.out.println(all_accounts.get(i-1)[3]);
                delete_accounts.add(all_accounts.get(i-1)[3]);
            }
            Db.deleteAccount(delete_accounts);
        } catch (RuntimeException e) {
            return;
        }
        System.out.println("Account(s) deleted successfully");
    }

    public String ArrayListToStringQuery(ArrayList<String> options) {
        String query = "(";
        for (int i = 0; i < options.size(); i++) {
            query += "'" + options.get(i) + "'";
            if (i != options.size() - 1)
                query += ", ";
        }
        query += ")";
        System.out.println(query);
        return query;
    }

    public ArrayList<Integer> deleteMultipleAccount(int limit) {
        ArrayList<Integer> list = new ArrayList<>();
        HashSet<Integer> selected = new HashSet<>();
        int start = 1;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter Account " + start + ": ");
            String inp = scanner.nextLine();

            if (inp.trim().isEmpty()) {
                if (list.isEmpty()) {
                    System.out.println("No Accounts Selected");
                    menuH.doYouWantToReturnToPreviousMenu();
                    continue;
                }
                break;
            }
            int val = Integer.parseInt(inp);

            if (val <= 0 || val > limit) {
                System.out.println("Invalid Selection");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (selected.contains(val)) {
                System.out.println("Account Already Selected");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            }
            selected.add(val);
            list.add(val);
            start++;
            if (start == limit + 1) {
                System.out.println("All Passwords Are Selected");
                break;
            }
        }
        return list;
    }

    private String formatString(String str) {
        int len = str.length();
        int space = 1;
        int total = 30;

        // Leading space Generator
        int middle = (total - len) / 2;
        while (space++ < middle) {
            str = " " + str;
        }
        space += len;
        // Trailing space Generator
        while (space++ <= total) {
            str = str + " ";
        }
        return str;
    }

    public String decryptPassword(String encryptedPassword, String encryptedKey) {
        String decryptedKey = decryptKey(encryptedKey);
        String decryptedPassword = AES.decrypt(encryptedPassword, decryptedKey);
        return decryptedPassword;
    }

    public void myPasswords() {
        MenuHandler menuH = new MenuHandler();

        while (true) {
            try {
                ArrayList<String[]> myPasswords = Db.getAccountDetailsCreatedByMe();
                if (myPasswords.isEmpty()) {
                    System.out.println("**NO ACCOUNT PASSWORDS FOUND**");
                } else {
                    System.out.println("Select the account to view advance menu.");
                }
                int noOfPasswords = myPasswords.size();
                String[] accounts = addOnlyAccountDetails(myPasswords, false);
                String[] menu = new String[noOfPasswords + 4];
                for (int i = 0; i < noOfPasswords; i++)
                    menu[i] = accounts[i];

                menu[noOfPasswords] = "Create Password";
                menu[noOfPasswords + 1] = "Share Password(S)";
                menu[noOfPasswords + 2] = "Delete Password(S)";
                menu[noOfPasswords + 3] = "Return to Previous Menu";

                int choice = menuH.menuPrinterAndSelectionReturner(menu, true);
                System.out.println(choice);

                if (choice > 0 && choice <= noOfPasswords) {
                    // a password is selected
                    myPasswordAdvanceMenu(myPasswords.get(choice - 1));
                    continue;

                } else if (choice == noOfPasswords + 1) {
                    // create password
                    addPasswordAccount();
                    continue;
                } else if (choice == noOfPasswords + 2) {
                    // Share password(s)
                    sharePassword();
                    continue;
                } else if (choice == noOfPasswords + 3) {
                    // Delete password(S)
                    deleteAccount();
                    continue;
                } else {
                   break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return;
    }

    public void myPasswordAdvanceMenu(String[] account) {
        String[] menu = new String[] { "View Password", "Share Password", "Modify Password", "View Sharee",
                "Revoke Access To All", "Delete Password", "Back to Previous Menu" };

        // accountName,username,encryptedPassword,resId,encryptedKey

        boolean printList = true;
        while (true) {
            int choice = 0;
            try {
                choice = menuH.menuPrinterAndSelectionReturner(menu, printList);

            } catch (Exception e) {
                continue;
            }
            if (choice == 1) {
                System.out.println(
                        "UserName : " + account[1] + "\t Password: " + decryptPassword(account[2], account[4]));
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            } else if (choice == 2) {
                sharePassword(account);
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            } else if (choice == 3) {
                updatePassword(account);
                break;
            } else if (choice == 4) {
                // sharee
                listSharee(account);
                continue;
            } else if (choice == 5) {
                // revoke access to all
                Db.revokeAccessOfThisAccountToAllOtherUsers(account[3]);
                System.out.println("Account Access Revoked For All Users");
                menuH.doYouWantToReturnToPreviousMenu();
                continue;
            } else if (choice == 6) {
                // delete password
                Db.deleteAccount(account[3]);
                System.out.println("Account Removed Successfully");
                return;
            } else if (choice == 7) {
                return;
            }
            break;
        }
    }

    public String decryptKey(String encryptedKey) {
        String decryptedKey = "";
        try {
            decryptedKey = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
        } catch (Exception e) {
            System.out.println("Decrypting error: " + e);
        }
        return decryptedKey;
    }

    public void sharePassword(String[] account) {
        new UserManagement().listUsers();
        try {
            ArrayList<String> all_users = new UserManagementQueries().getUsernameList();
            // accountName,username,encryptedPassword,resId,encryptedKey
            ArrayList<Integer> user =  selectMultipleUser(all_users.size()-1);
            String res_id = account[3];
            String encryptedKey = account[4];
            String decryptedKey = "";
            try {
                decryptedKey = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
            } catch (Exception e) {
                System.out.println("Sharing error: " + e);
            }

            for (int iterator : user) {
                String sharee = all_users.get(iterator - 1);
                System.out.println(sharee);
                if (Db.checkIfAlreadyShared(sharee, res_id) || sharee.equals(uo.getUserId())) {
                    continue;
                }
                String shareePublicKey = new UserManagementQueries().getPublicKey(sharee);
                String encryptedKeyForSharee = RSAUtil.getEncryptedString(decryptedKey, shareePublicKey);
                System.out.println("Shared Encrypted Key: " + encryptedKeyForSharee);
                Db.shareAccount(sharee, res_id, encryptedKeyForSharee);
            }

            System.out.println("Account successfully shared with selected user(s)");
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    public void updatePassword(String[] account) {
        // accountName,username,encryptedPassword,resId,encryptedKey
        try {
            String currentAccountName = account[0];
            String res_id = account[3];

            ReadAccountDetailsForUpdate readDetails = new ReadAccountDetailsForUpdate();
            // get update
            String newAccountName = readDetails.readNewAccountName(currentAccountName);
            String newUsername = readDetails.readNewUsername();
            String newPassword = readDetails.readNewPassword();

            // check if changed

            boolean checkIfNameChanged = !(newAccountName == null); // TRUE -> YES
            boolean checkIfUsernameChanged = !(newUsername == null);
            boolean checkIfPasswordChanged = !(newPassword == null);

            if (checkIfPasswordChanged) {
                String encryptedKey = account[4];
                String key = "";
                try {
                    key = RSAUtil.decrypt(encryptedKey, uo.getPrivateKey());
                } catch (Exception e) {
                }
                newPassword = AES.encrypt(newPassword, key);
            }

            if (checkIfNameChanged || checkIfUsernameChanged || checkIfPasswordChanged) {
                Db.updateAccountDetails(newAccountName, newUsername, newPassword, res_id);
                System.out.println("Account Updated successfully.");

            } else {
                System.out.println("No new changes detected.");
            }
        } catch (Exception e) {
        }

    }

    public void listSharee(String[] account) {
        // accountName,username,encryptedPassword,resId,encryptedKey
        while (true) {
            ArrayList<String> sharee = Db.listOfShareeOfAnAccount(account[3]);
            if (sharee.isEmpty()) {
                System.out.println("Account Not shared with anyone");
                return;
            }
            int noOfSharee = sharee.size();
            sharee.add("Back to Previous Menu");
            System.out.println("Select sharee to revoke access to '" + account[0].toUpperCase());

            int choice2 = menuH.menuPrinterAndSelectionReturner(menuH.arrayListToStringArrayConverter(sharee), true);
            if (choice2 == sharee.size()) {
                return;
            } else {
                String sharee_id = sharee.get(choice2 - 1);
                String res_id = account[3];
                Db.revokeAccessOfAccountToUser(res_id, sharee_id);
                if (noOfSharee > 1) {
                    if (menuH.doYouWantTo("revoke access to another sharee")) {
                        continue;
                    }
                } else {
                    return;
                }
            }
            break;
        }
    }
}
