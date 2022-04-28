package Management;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import IOClasses.*;
import Database.*;
import Users.UsersObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import static java.lang.Math.abs;

/**
 *
 * @author rarun
 */
public class UserManagement {

    UserManagementQueries Db = new UserManagementQueries();
    Scanner sc = scanner.scan;
    static Random r = new Random();
    UsersObject uo = UsersObject.getInstance();
    String current_username = uo.getUserId();
    String user_type = uo.getAccountType();
    ReadVerifyUserCredentials ruc = new ReadVerifyUserCredentials();

    public String[] readVerifyUserIdToRemove() {
        MenuHandler m = new MenuHandler();
        sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the user id that need to be removed :");
            String id = sc.nextLine();
            id = id.trim();
            if (id.isEmpty()) {
                System.out.println("User id cannot be empty");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (!id.matches("^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
                System.out.println("Invalid username!");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if (!new LoginQueries().isValidUserId(id)) {
                System.out.println("User id not found");
                m.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return new String[] { id, Db.getUserType(id) };
        }
    }

    public void cancelAllInvitesFromTheUser(String user_id) {
        // Remove all invites from the user
        Db.cancelAllInvitesFromTheUser(user_id);
    }

    public void removePasswordsCreatedAndSharedByUser(String user_id) {

        PasswordManagementQueries Db = new PasswordManagementQueries();
        // First revoke access of all passwords shared by the user
        Db.revokeAccessOfAllAcccountsSharedByTheUser(user_id);
        // revoke access of all passwords shared to the user
        Db.revokeAccessOfAllAccountsSharedToTheUser(user_id);
        // Delete all password created by the user
        Db.deleteAccountsCreatedByTheUser(user_id);
    }

    public void removeUser() {
        try {
            int type = getCurrentUserType();

            if (type == 1) {
                System.out.println("Access Denied.");
                return;
            }

            String[] res = readVerifyUserIdToRemove();
            if (res.length == 0) {
                return;
            }

            String selected_user = res[0];
            String selected_user_type = res[1];
            if (selected_user.equals(current_username)) {
                System.out.println("Cannot remove current user.");
                return;
            }
            if (Db.getOrganizationIdOfUser(selected_user).equals(uo.getOrganization())) {
                if (type == 2) {
                    if (selected_user_type.equals("superadmin")) {
                        System.out.println("You dont have access to remove the selected user");
                        return;
                    }
                }

                if (!new MenuHandler().doYouWantTo("remove " + selected_user.toUpperCase() + " from your organization")) {
                    System.out.println("Removal operation cancelled.");
                    return;
                }

                // this.cancelAllInvitesFromTheUser(selected_user);
                // this.removePasswordsCreatedAndSharedByUser(selected_user);

                int stat = Db.removeUserQuery(selected_user);
                if (stat != -1) {
                    System.out.println("user " + selected_user + " removed successfully");
                } else {
                    System.out.println("Removal operation failed");
                }
            } else {
                System.out.println("User does not exist.");
            }
        } catch (RuntimeException e) {
        }
    }

    public void removeMultipleUsers() {
        listUsers();
        try {
            ReadVerifyUserCredentials readUser = new ReadVerifyUserCredentials();
            ArrayList<String[]> users = readUser.readVerifyMultipleUsername("remove", "user");
            int stat = Db.removeMultipleUsers(users);
            if (stat != -1) {
                System.out.println("user(s) removed successfully");
            } else {
                System.out.println("Removal operation failed");
            }
        } catch (Exception e) {
        }
    }

    public String ArrayListToStringQuery(ArrayList<String[]> users) {
        String query = "(";
        for (String[] user : users) {
            query += "'" + user[0] + "'";
            if (!(users.indexOf(user) == users.size() - 1)) {
                query += " , ";
            }
        }
        query += ")";
        return query;
    }

    public void printListOfUsers(ArrayList<String> list) {
        MenuHandler menu = new MenuHandler();
        String[] strArrList = menu.arrayListToStringArrayConverter(list);
        menu.maxLengthInList(strArrList);
        menu.printHeading("**List of Users**");
        menu.printList(strArrList);
    }

    public void cancelInvite() {
        MenuHandler menu = new MenuHandler();
        try {
            ArrayList<String[]> myInvites = getMyPendingInvites();
            String[] myinvites = formatInvites(myInvites, true);
            System.out.println("Select the invite which you want to cancel");
            int choice = menu.menuPrinterAndSelectionReturner(this.getHeaderForPrintingInvites(), myinvites, true);
            if (choice - 1 == myInvites.size())
                return;
            String code = myInvites.get(choice - 1)[2];
            Db.cancelInvite(code);
            System.out.println("Invite to " + myInvites.get(choice - 1)[0].toUpperCase() + " cancelled successfully");
        } catch (RuntimeException e) {
        }
    }

    private ArrayList<String[]> getMyPendingInvites() {
        return Db.myPendingInvites();
    }

    public String[] formatInvites(ArrayList<String[]> myInvites, boolean enableSelection) {
        int size = myInvites.size();
        if (enableSelection)
            size += 1;
        String[] myinvites = new String[size];
        // invitee, invited_on,link,user_type
        for (int i = 0; i < myInvites.size(); i++) {
            String[] temp = myInvites.get(i);
            String print = formatString(temp[0]) + "|" + formatString(temp[1]) + "|" + formatString(temp[2]) + "|"
                    + formatString(temp[3]);
            myinvites[i] = print;
        }
        if (enableSelection) {
            myinvites[size - 1] = "Return to Previous Menu";
        }
        return myinvites;
    }

    public void listOfMyPendingInvites() {
        ArrayList<String[]> myInvites = getMyPendingInvites();
        if (myInvites.isEmpty()) {
            System.out.println("*****No Pending Invites*****");
            return;
        }
        String[] myinvites = formatInvites(myInvites, false);
        String header = getHeaderForPrintingInvites();
        int max = header.length();

        MenuHandler mh = new MenuHandler();

        mh.max_length = max;
        mh.maxLengthInList(myinvites);
        mh.printHeading(header);
        mh.printList(myinvites);
    }

    public String getHeaderForPrintingInvites() {
        return "   " + formatString("Invitee ID") + "|" + formatString("Invited On") + "|" + formatString("Invite Code")
                + "|" + formatString("Invited As");
    }

    private String formatString(String str) {
        int len = str.length();
        int space = 1;

        // Leading space Generator
        int middle = (30 - len) / 2;
        while (space++ < middle) {
            str = " " + str;
        }
        space += len;
        // Trailing space Generator
        while (space++ <= 30) {
            str = str + " ";
        }
        return str;
    }

    public void listUsers() {
        int type = getCurrentUserType();

        if (type == 1) {
            System.out.println("Access Denied.");
            return;
        }
        ArrayList<String> list = Db.getListOfUsersQuery();
        printListOfUsers(list);
    }

    public int getCurrentUserType() {
        int type = 1;

        if (user_type.equals("superadmin")) {
            type = 3;
        } else if (user_type.equals("admin")) {
            type = 2;
        }
        return type;
    }


    public void inviteUser() {
        try {
            String new_user_id = ruc.readVerifyUsername();
            if (Db.checkIfAlreadyInvited(new_user_id, current_username)) {
                System.out.println("You already invited this user.");
                return;
            }
            if (Db.checkIfAlreadyInvitedFromCurrentOrganization(new_user_id)) {
                System.out.println("User already invited from your organization");
                return;
            }
            String new_user_type = ruc.readVerifyUserType(getCurrentUserType());
            String invite_link = inviteLinkGenerator(new_user_id);
            Db.inviteUser(new_user_id, new_user_type, invite_link);
            System.out.println("Generated invitation code: " + invite_link);
            System.out.println("Invitation successful");

        } catch (Exception e) {
        }
    }

    public void inviteMultipleUsers() {
        try {
            ArrayList<String[]> invitees = ruc.readVerifyMultipleUsername("invite", "invitee");
            for (String[] invitee : invitees) {
                String username = invitee[0];
                String userType = invitee[1];
                String invite_link = inviteLinkGenerator(username);
                Db.inviteUser(username, userType, invite_link);
                System.out.println("Generated invitation code: " + invite_link);
                System.out.println("Invitation to " + username + " successful");
            }
        } catch (Exception e) {
        }
    }

    private char randomCharacterGenerator(char c) {
        int ch = r.nextInt(abs(c - 90)) + 64;
        return (char) ch;
    }

    private String inviteLinkGenerator(String invitee) {
        String admin = uo.getUserId();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String out = admin + formatter.format(date)+invitee;
        String link = "";
        for (int i = 0; i < out.length(); i++) {
            link += randomCharacterGenerator(out.charAt(i));
        }
        return link;
    }

}
