/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Menus;

import IOClasses.MenuHandler;
import Management.*;
import Users.UsersObject;
import java.util.*;

/**
 *
 * @author rarun
 */
public class Menu {

    String type;
    MenuHandler p = new MenuHandler();

    public Menu() {
        UsersObject uo = UsersObject.getInstance();
        type = uo.getAccountType();
    }

    void functionFinder(String Operation) {

        ArrayList<String> operations = new ArrayList<>();
        operations.add("Admin Tools"); //0
        operations.add("Password Management"); //1
        operations.add("Share Password");//2
        operations.add("Remove User");//3
        operations.add("List Users");//4
        operations.add("Invite User");//5
        operations.add("Add password account");//6
        operations.add("modify password account");//7
        operations.add("delete password account");//8
        operations.add("list passwords shared by me");//9
        operations.add("list passwords shared to me");//10
        operations.add("View all passwords");//11
        operations.add("list of pending invites");//12
        operations.add("cancel invite");//13

        int indx = -1;
        for (int i = 0; i < operations.size(); i++) {
            if (operations.get(i).toLowerCase().equals(Operation)) {
                indx = i;
                break;
            }
        }

        UserManagement userManager = new UserManagement();
        MenuManagement MenuManager = new MenuManagement();
        PasswordManagement PassManager = new PasswordManagement();

        switch (indx) {
            case 0: {
                MenuManager.adminTools();
                break;
            }
            case 1: {
                MenuManager.passwordManagement();
                break;
            }
            case 2: {
                PassManager.sharePassword();
                break;
            }
            case 3: {
                userManager.removeUser();
                break;
            }
            case 4: {
                userManager.listUsers();
                break;
            }
            case 5: {
                userManager.inviteUser();
                break;
            }
            case 6: {
                PassManager.addPasswordAccount();
                break;
            }
            case 7: {
                PassManager.modifyAccount();
                break;
            }
            case 8: {
                PassManager.deleteAccount();
                break;
            }
            case 9: {
                PassManager.listPasswordsSharedByMe();
                break;
            }
            case 10: {
                PassManager.listPasswordsSharedToMe();
                break;
            }
            case 11: {
                PassManager.listAllPasswords();
                break;
            }
            case 12: {
                userManager.listOfMyPendingInvites();
                break;
            }
            case 13: {
                userManager.cancelInvite();
                break;
            }
            default: {
                System.out.println("Operations Not yet Implemented");
                break;
            }
        }
    }

    public void flowControl(String heading, TreeMap<Integer, String> menu) {
        ArrayList<String> user_menu = new ArrayList<>();
        for (Map.Entry<Integer, String> m : menu.entrySet()) {
            user_menu.add(m.getValue().toString());
        }
        while (true) {
            try {
                int ch = p.menuPrinterAndSelectionReturner(heading, user_menu, true);

                String operation = user_menu.get(ch - 1).toLowerCase();
                if (operation.equals("logout") || operation.equals("return to main menu")) {
                    return;
                }
                functionFinder(operation);
            } catch (Exception e) {
            }
        }
    }

    public void printMainMenu() {
        new MenuHandler().headerPrinter();

        TreeMap<Integer, String> menu = new TreeMap<>();
        menu.put(2, "Password Management");
        menu.put(4, "Account Settings");
        menu.put(5, "Logout");

        if (!(type.equals("user"))) {
            menu.put(1, "Admin Tools");
        }
        flowControl("**Main Menu**", menu);
    }
}
