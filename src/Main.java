
import IOClasses.MenuHandler;
import Menus.*;

/**
 *
 * @author rarun
 */
public class Main {
    public static void main(String[] args) {
        String[] menu = new String[]{"Login", "Register", "Signup With Invite Link", "Exit"};
        MenuHandler print = new MenuHandler();

        while (true) {
            try {
                int ch = print.menuPrinterAndSelectionReturner("*Password Manager*", menu, true);
                switch (ch) {
                    // case 1:
                    //     new Login();
                    //     break;
                    case 2:
                        new Registration();
                        break;
                    case 3:
                         new Invitation();
                        break;
                    case 4:
                        System.out.println("Exiting Application...");
                        System.exit(0);
                }

            } catch (Exception e) {}
        }

    }
}
