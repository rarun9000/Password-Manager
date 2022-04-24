/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IOClasses;

import Users.UsersObject;
import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author rarun
 */
public class MenuHandler {

    Scanner s = scanner.scan;
    public int max_length = 25;

    public void printList(String[] list) {
        int start = 1;
        System.out.print("+");
        for (int st = 1; st < max_length; st++) {
            System.out.print("-");
        }
        System.out.print("+\n");

        for (String menu : list) {
            System.out.println(formatString(start + ". " + menu, max_length));
            start++;
        }

        System.out.print("+");
        for (int st = 1; st < max_length; st++) {
            System.out.print("-");
        }
        System.out.print("+\n");
    }

    private String formatString(String str, int t_space) {
        int len = str.length();
        int space = 1;
        //Leading space Generator
        int middle = (t_space - len) / 2;
        while (space++ < middle) {
            str = " " + str;
        }
        space += len;
        //Trailing space Generator
        while (space++ <= t_space) {
            str = str + " ";
        }

        return "|" + str + "|";
    }

    public void maxLengthInList(String[] list) {
        int maxi = 35;
        for (String str : list) {
            maxi = max(maxi, str.length());
        }
        max_length = max(max_length, maxi + 4);
    }

    public void printHeading(String head) {

        max_length = max(max_length, head.length() + 4);

        System.out.print("+");
        for (int st = 1; st < max_length; st++) {
            System.out.print("-");
        }
        System.out.print("+\n");
        System.out.println(formatString(head, max_length));
    }

    public int menuPrinterAndSelectionReturner(String[] list, boolean printlist) {
        if (printlist) {
            maxLengthInList(list);
            printList(list);
        }
        s = new Scanner(System.in);

        while (true) {
            //First value -> 1 if the selected option is valid -1 if the selected option is invalid
            //Second Value -> The selected option number
            System.out.println("Enter your choice\t:  ");
            String choice = s.nextLine();
            choice = choice.trim().toLowerCase();
            
            if (!hasOnlyNumbers(choice)) {
                System.out.println("Selected option must be a Digit.");
                this.doYouWantToReturnToPreviousMenu();
                continue;
            }
            
            if (choice.length() > 2) {
                System.out.println("Invalid option. Option must be from the above list.");
                this.doYouWantToReturnToPreviousMenu();
                continue;
            }
            
            int res = Integer.parseInt(choice);

            if (res > list.length || res <= 0) {
                System.out.println("Invalid option. Option must be from the above list.");
                this.doYouWantToReturnToPreviousMenu();
                continue;
            }
            return res;
        }
    }

    public int menuPrinterAndSelectionReturner(String heading, String[] list, boolean printlist) {
        maxLengthInList(list);
        printHeading(heading);
        return menuPrinterAndSelectionReturner(list, printlist);
    }

    public int menuPrinterAndSelectionReturner(String heading, ArrayList<String> list, boolean printlist) {
        return menuPrinterAndSelectionReturner(heading, arrayListToStringArrayConverter(list), printlist);
    }

    public int menuPrinterAndSelectionReturner(ArrayList<String> list, boolean printlist) {
        return menuPrinterAndSelectionReturner(arrayListToStringArrayConverter(list), printlist);
    }

    public String[] arrayListToStringArrayConverter(ArrayList<String> menu) {
        String[] arr = new String[menu.size()];
        int i = 0;
        for (String x : menu) {
            arr[i] = x;
            i++;
        }
        return arr;
    }

    public void headerPrinter() {
        UsersObject temp = UsersObject.getInstance();

        String lastlogin = temp.getLastLogin();
        String type = temp.getAccountType();
        String userid = temp.getUserId();

        String last_login_details = "Last Login: " + ((lastlogin == null) ? "Never" : lastlogin);
        String user_type = "*** " + type + "'s Menu ***";
        String user_name = "Current User : " + userid;

        String[] list = new String[]{last_login_details, user_type, user_name};
        this.maxLengthInList(list);
        last_login_details = this.formatString(last_login_details, max_length);
        user_type = this.formatString(user_type, max_length);
        user_name = this.formatString(user_name, max_length);
        System.out.println("\n" + last_login_details + "\n" + user_type + "\n" + user_name + "\n");
    }

    public void doYouWantoExit() {
        System.out.print("\nDo you want to exit application?(Y/N)");
        if (yesOrNo()) {
            System.exit(0);
        }
    }

    public void doYouWantToReturnToPreviousMenu() {
        System.out.print("\nDo you want to try again?(Y/N)");
        if (!yesOrNo()) {
            throw new RuntimeException("Returning to previous menu");
        }
    }

    public boolean yesOrNo() {
        s = new Scanner(System.in);
        while (true) {
            String choice = s.nextLine();
            choice = choice.trim().toLowerCase();
            if (hasOnlyNumbers(choice)) {
                System.out.println("Selected choice can only be a letter. Try again.");
                continue;
            }
            if (choice.length() != 1) {
                System.out.println("Invalid choice. Enter 'Y' or 'N' :");
                continue;
            }
            char x = choice.charAt(0);
            if (x != 'n' && x != 'y') {
                System.out.println("Invalid choice. Enter either 'Y' or 'N' :");
                continue;
            }
            System.out.println();
            if (x == 'y') {
                return true; // yes
            }
            break;
        }
        return false; // No
    }

    public boolean doYouWantTo(String option) {
        System.out.print("\nDo you want to " + option + "?(Y/N)");
        return yesOrNo();
    }

    public static boolean hasOnlyAlphabets(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static boolean hasOnlyNumbers(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[0-9]*$")));
    }

}
