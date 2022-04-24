/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Menus;

import Database.InvitationQueries;
import Database.LoginQueries;
import Database.UserManagementQueries;
import IOClasses.MenuHandler;
import IOClasses.ReadVerifyUserCredentials;
import IOClasses.scanner;
import Users.UsersObject;
import java.util.Scanner;

/**
 *
 * @author rarun
 */
public class Invitation {

    public Invitation() {
        startProcess();
    }
    //Re1ad invitaation link
    Scanner scan = scanner.scan;

    public String readVerifyInviteLink() {

        InvitationQueries iq = new InvitationQueries();
        MenuHandler menuio = new MenuHandler();

        System.out.println("Enter the invitation Link: ");
        scan = new Scanner(System.in);
        while (true) {
            String invite_link = scan.nextLine();
            if (!iq.verifyInviteLink(invite_link)) {
                menuio.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return invite_link;
        }
    }

    private void printDetails(String[] details) {
        System.out.println("You are invited by: " + details[0] + " from " + details[3] + " as a " + details[2]);
        System.out.println("Your username: " + details[1]);
    }

    public final void startProcess() {
        try {
            ReadVerifyUserCredentials readcred = new ReadVerifyUserCredentials();
            InvitationQueries iq = new InvitationQueries();

            String link = readVerifyInviteLink();
            String[] details = iq.getInviteDetails(link);
            LoginQueries loginq = new LoginQueries();

            //inviter, invitee , type, organization
            printDetails(details);

            if (loginq.isValidUserId(details[1])) {
                System.out.println("Account already exists!");
                return;
            }
            
            String pass = readcred.readVerifyPassword();

            UserManagementQueries userqueries = new UserManagementQueries();
            userqueries.addUserQuery(details[1], pass, details[2], details[3]);
            //remove the invitation from invite table
           
            userqueries.cancelInvite(link);
            
            System.out.println("Joined the organization successfully!. Logging into your dashboard");

            String[] data = {details[1], pass, details[2], null, details[3]};
            UsersObject.getInstance(data);
            Menu m = new Menu();
            m.printMainMenu();
        } 
        catch(RuntimeException e){
            
        }
    }
}
