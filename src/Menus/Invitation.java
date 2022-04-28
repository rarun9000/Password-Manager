/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Menus;

import Database.InvitationQueries;
import Database.LoginQueries;
import Database.RegistrationQueries;
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
    MenuHandler menuio = new MenuHandler();

    public String readVerifyInviteLink() {

        InvitationQueries iq = new InvitationQueries();

        System.out.println("Enter the invitation Link: ");
        scan = new Scanner(System.in);
        while (true) {
            String invite_link = scan.nextLine().trim();
            if (!iq.verifyInviteLink(invite_link)) {
                System.out.println("We couldn't find your invite link.");
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

    public String readVerifyName(){
        System.out.println("Enter your name: ");
        scan  = new Scanner(System.in);
        while(true){
            String name = scan.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("Name cannot be empty");
                menuio.doYouWantToReturnToPreviousMenu();
                continue;
            }
            if(name.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$") == false){
                System.out.println("Invalid Name");
                menuio.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return name;
        }
    }


    public final void startProcess() {
        try {
            ReadVerifyUserCredentials readcred = new ReadVerifyUserCredentials();
            InvitationQueries iq = new InvitationQueries();

            String link = readVerifyInviteLink();
            String[] details = iq.getInviteDetails(link);
            LoginQueries loginq = new LoginQueries();

            //inviter, invitee , type, organization , orgId
            printDetails(details);

            if (loginq.isValidUserId(details[1])) {
                System.out.println("Account already exists!");
                return;
            }
            String name = readVerifyName();
            String pass = readcred.readVerifyPassword();

            RegistrationQueries queries = new RegistrationQueries();
            queries.createUser(details[1], name, details[2], details[4], pass);
            //userqueries.addUserQuery(details[1], pass, details[2], details[3]);
            //remove the invitation from invite table
           
            new UserManagementQueries().cancelInvite(link);
            
            System.out.println("Joined the organization successfully!. Logging into your dashboard");

            String[] data = {details[1], pass, details[2], null, details[4]};
            UsersObject.getInstance(data);
            Menu m = new Menu();
            m.printMainMenu();
        } 
        catch(RuntimeException e){
            
        }
    }
}
