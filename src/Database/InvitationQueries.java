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
    String table_name = "invites";

    public boolean verifyInviteLink(String link) {
        try {
            String query = "select * from "+table_name+" where link = ?";            
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
        String[] result = new String[5];
        //invited_by, invited_to , type, organization
        try {
            String query = "select * from "+table_name+" where link = ?";
            ResultSet rs = executeQuery(query,new String[]{link});
            if (rs.next()) {
                result[0] = rs.getString("inviter");
                result[1] = rs.getString("invitee");
                result[2] = rs.getString("role");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] org = getOrganization(result[0]);
        result[3] = org[0];
        result[4] = org[1];
        return result;
    }
    
    public void deleteInvitation(String link){
        String query = "delete from "+table_name+" where link = ?";
        updateQuery(query,new String[]{link});
    }
    
    public String[] getOrganization(String user_id) {
        String query = "select orgName, o.orgId from user_details u , organization_details o where user_id = ? and u.orgId = o.orgId";
        String[] orgDetails = new String[2];
        try {
            ResultSet rs = executeQuery(query,new String[]{user_id});
            if (rs.next()) {
                orgDetails[0] = rs.getString("orgName");
                orgDetails[1] = rs.getString("orgId");

            }
        } catch (Exception e) {
            System.out.println("Error in finding organization");
        }
        return orgDetails;
    }
}
