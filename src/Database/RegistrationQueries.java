/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.ResultSet;

/**
 *
 * @author rarun
 */
public class RegistrationQueries extends Sql {
    /*
    List of functions
        1. AddOrganization()
        2. isValidOrganization()
    */

    public int addOrganization(String username, String password, String org_name) {
        while (true) {
            String query = "insert into users values(?,?,?,?,?)";
            
            int stat = updateQuery(query, new String[]{username, password.hashCode()+"", "superadmin",null,org_name});
            if (stat == -1) {
                System.out.println("Registration Unsuccessful! Try Again");
                continue;
            }
            System.out.println("Registration Successful!");
            return 1;
        }
    }

    public boolean isValidOrganization(String org_name) {
        String query = "select * from users where organization= ?";
        ResultSet rs = executeQuery(query,new String[]{org_name} );
        try {
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error ee: " + e);
        }
        return false;
    }
}
