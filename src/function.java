import java.util.ArrayList;
import java.util.HashSet;

import IOClasses.MenuHandler;

public class function {
    public static void main(String[] args) {
        String title = "Sharee";
        String list[] = new String[] { "pradeep", "pradeep2", "Pradeep3" };
        ArrayList<String> res = selectMultipleFromList(title, list);
        for (String x : res) {
            System.out.println(list[Integer.parseInt(x) - 1]);
        }
    }

    public static ArrayList<String> selectMultipleFromList(String title, String[] List) {
        ArrayList<String> res = new ArrayList<>();
        HashSet<Integer> options = new HashSet<>();
        MenuHandler mh = new MenuHandler();
        boolean b = true;
        int start = 1;
        while (true) {
            if (b) {
                b = false;
                System.out.println("Select the list of" + title
                        + ".\nLeave the input empty and press enter to end the input process.");
            }
            try {
                int option = mh.menuPrinterAndSelectionReturner(title, start, List, false);
                if (option == -1) {
                    if (res.isEmpty()) {
                        System.out.println("No selections found");
                        if (mh.doYouWantTo("select again")) {
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        if (mh.doYouWantTo("select more " + title)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                if (options.contains(option)) {
                    System.out.println("Option already selected");
                    if (mh.doYouWantTo("select again")) {
                        continue;
                    } else {
                        break;
                    }
                }
                res.add(option + "");
                options.add(option);
                start++;
                continue;
            } catch (RuntimeException e) {
                break;
            }
        }
        return res;
    }
}
