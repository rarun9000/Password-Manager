import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.plaf.synth.SynthStyle;

import IOClasses.MenuHandler;

public class function {
    public static void main(String[] args) {
        MenuHandler m = new MenuHandler();
        int choice = m.menuPrinterAndSelectionReturner("User role", new String[] { "user", "admin", "superadmin" },
                true);
        System.out.println(choice);
    }

}
