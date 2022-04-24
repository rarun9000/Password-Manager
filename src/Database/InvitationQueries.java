/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rarun
 */
public class InvitationQueries extends Sql {
/*
    List of functions
        1. VerifyInviteLink()
        2. getInviteDetails()
        3. UpdateInvitation()
        4. getOrganization()
    */
    public boolean verifyInviteLink(String link) {
        try {
            String query = "select * from invites where link = ?";            
            ResultSet rs = executeQuery(query, new String[]{link});
            if (rs.next()) {
                return true;
            } 
        } catch (SQLException ex) {
            Logger.getLogger(InvitationQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String[] getInviteDetails(String link) {
        String[] result = new String[4];
        //invited_by, invited_to , type, organization
        try {
            String query = "select * from invites where link = ?";
            ResultSet rs = executeQuery(query,new String[]{link});
            if (rs.next()) {
                result[0] = rs.getString("inviter");
                result[1] = rs.getString("invitee");
                result[2] = rs.getString("user_type");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        result[3] = getOrganization(result[0]);
        return result;
    }
    
    public void deleteInvitation(String link){
        String query = "delete from invites where link = ?";
        updateQuery(query,new String[]{link});
    }
    
    public String getOrganization(String user_id) {
        String query = "select * from users where user_id = ? ";
        String org = "";
        try {
            ResultSet rs = executeQuery(query,new String[]{user_id});
            if (rs.next()) {
                org = rs.getString("organization");
            }
        } catch (Exception e) {
            System.out.println("Error in finding organization");
        }
        return org;
    }
}
