/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management;

import Menus.*;
import Users.UsersObject;
import java.util.*;

/**
 *
 * @author rarun
 */
public class MenuManagement {

    UsersObject uo = UsersObject.getInstance();
    String type = uo.getAccountType();
    /*
    List of Menus:
        1. Admin Tools
        2. Password Management
        3. Personal Passwords
        4. Logout
        (Not yet Added to Idea)
        5. Settings -> Change Password, Delete Account(For SuperAdmin). 
     */
    Menu m = new Menu();

    public void adminTools() {
        TreeMap<Integer, String> menu = new TreeMap<>();
        if (type.equals("superadmin")) {
            menu.put(23, "Invite User");
            menu.put(20, "List of Pending Invites");
            menu.put(25, "Cancel Invite");
        }
        menu.put(40, "Remove User");
        menu.put(1, "List Users");
        menu.put(100, "Return to main menu");
        m.flowControl("***Admin Tools***", menu);
    }

    public void passwordManagement() {
        /*
            ----Admin & Super Admin options---
            1. Add Password Account
            2. Modify Password Account
            3. Delete Password Account
            4. List Passwords Shared By Me
            5. List Passwords Shared To Me
            6. View All Passwords.
            7. Return To Main Menu
         */

        TreeMap<Integer, String> menu = new TreeMap<>();

        menu.put(1, "View All Passwords");
        menu.put(100, "Return to main menu");
        menu.put(30, "List Passwords Shared By Me");
        menu.put(20, "List Passwords Shared To Me");
        menu.put(40, "Add Password Account");
        menu.put(50, "Modify Password Account");
        menu.put(60, "Delete Password Account");
        menu.put(2, "Share Password");
        m.flowControl("***Password Management***", menu);
    }
}
