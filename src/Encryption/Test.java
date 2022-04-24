/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;

// import IOClasses.scanner;
// import Management.MenuManagement;
// import Management.PasswordManagement;
// import Users.UsersObject;
// import static java.lang.Math.abs;
// import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author rarun
 */
public class Test {

    static Random r = new Random();

    public static void main(String[] args) {
        while (true) {
            Scanner s = new Scanner(System.in);
            System.out.println("Name:");
            String name = s.nextLine();
            if(name.trim().isEmpty())
                name = null;
            System.out.println("Url:");
            String url = s.nextLine();
            if(url.trim().isEmpty())
                url = null;
            System.out.println("Username:");
            String username = s.nextLine();
            if(username.trim().isEmpty())
                username = null;
            System.out.println("Password:");
            String password = s.nextLine();
            if(password.trim().isEmpty())
                password = null;
            
            String nameAddon = (name != null) ? "name = ?" : "";
            //add , before url if name is not null
            String urlAddon =  ((url != null) ? ((name != null) ? "," : "") + "url = ?" : "");
            //add , before username if name is not null or url is not null
            String usernameAddon =  ((username != null) ?((name != null || url != null) ? "," : "") + "username = ?" : "");
            //add , before username if name is not null or url is not null or username is not null
            String passwordAddon =((password != null) ? ((name != null || url != null || username != null) ? "," : "") +  "password = ?" : "");

            String query = "update accounts set " + nameAddon + urlAddon + usernameAddon + passwordAddon + " where res_id  = ?";
            System.out.println(query);
            s.close();
        }
    }

    public static boolean strongPasswordCheck(String password) {
        if (password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$])[A-Za-z\\d@$#]{8,20}$")) {
            return true;
        }
        return false;
    }

    public static boolean hasOnlyNumbers(String str) {
        return ((str != null) && (!str.equals(""))
                && (str.matches("^[0-9]*$")));
    }
}
