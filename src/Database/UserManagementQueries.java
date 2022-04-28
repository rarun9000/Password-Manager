/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Users.UsersObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Management.UserManagement;

/**
 *
 * @author rarun
 */
public class UserManagementQueries extends Sql {

    UsersObject temp_object = UsersObject.getInstance();
    String org = temp_object.getOrganization();
    String tableName = "user_details";
    /* List of Functions
        1. UpdateLastLogin()
        2. InviteUser()
        3. CheckIfAlreadyInvited()
        4. UpdatePassword()
        5. RemoveUserQuery()
        6. GetUserType()
        7. AddUserQuery()
        8. ListUsersQuery()
     */
    
    public void updateLastLogin() {
        String query = "update "+tableName+" set last_login = (SELECT CURRENT_TIMESTAMP()) where user_id = ? ";// and organization ='"+org+"'";
        updateQuery(query, new String[]{temp_object.getUserId()});
    }

    public void cancelAllPendingInvitesToUser(String user){
        String query = "delete from invites where invitee = ?";
        updateQuery(query, new String[]{user});
    }
    public void cancelInviteToUserFromCurrentUser(String invitee){
        String query = "delete from invites where invitee = ? and inviter= ?";
        updateQuery(query,new String[]{invitee, temp_object.getUserId()});
    }
    public void cancelAllInvitesFromTheUser(String user){
        String query = "delete from invites where inviter = ?";
        updateQuery(query, new String[]{user});
    }
    public void cancelInvite(String invite_code){
        String query ="delete from invites where link = ?";
        updateQuery(query, new String[]{invite_code});
    }
    public void inviteUser(String new_user_id, String role, String link) {
        String query = "insert into invites(inviter,invitee,link,role) values( ? , ? , ? ,?)";
        updateQuery(query, new String[]{temp_object.getUserId(), new_user_id, link, role});
    }
    public ArrayList<String[]> myPendingInvites(){
        ArrayList<String[]> invites = new ArrayList<>();
        String query = "select invitee, invited_on,link,role from invites where inviter = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{temp_object.getUserId()});
            while(rs.next()) {
                String[] invite_data = new String[4];
                invite_data[0] = rs.getString("invitee");
                invite_data[1] = rs.getString("invited_on");
                invite_data[2] = rs.getString("link");
                invite_data[3] = rs.getString("role");
                invites.add(invite_data);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(InvitationQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return invites;
    }
    public boolean checkIfAlreadyInvited(String invitee, String inviter) {
        String query = "select * from invites where invitee = ? and inviter = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{invitee, inviter});
            if (rs.next()) {
                System.out.println("User already invited!");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void updatePassword(String username, String password) {
        String query = "update users set master_password = ? where user_id= ?";
        updateQuery(query,new String[]{password.hashCode()+"", username});
    }

    public int removeUserQuery(String user) {
        String query = "delete from "+tableName+" where user_id= ? and orgId = ?";
        return updateQuery(query,new String[]{user, org});
    }
    
    public String getOrganizationIdOfUser(String user_id){
        String query = "select od.orgId from organization_details od, user_details ud where user_id = ? and od.orgId = ud.orgId";
        try{
            ResultSet rs = executeQuery(query, new String[]{user_id});
            if(rs.next()){
                return rs.getString("orgId");
            }
        }
        catch(Exception e){
            System.out.println("getOrganization error");
        }
        return "";
    }

    public int removeMultipleUsers(ArrayList<String[]> users){
        String user = new UserManagement().ArrayListToStringQuery(users);
        String query  = "delete from users where user_id in "+user;
        return updateQuery(query);
    }

    public String getUserType(String username) {
        String query = "select role from "+tableName+" where user_id = ? ";
        ResultSet rs = executeQuery(query, new String[]{username});
        try {
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (Exception e) {
            System.out.println("Error in fetching  user type");
        }
        return "";
    }
    public String getLastLoginOfUser(String username){
        String query = "select last_login from "+tableName+" where user_id = ? ";
        ResultSet rs = executeQuery(query, new String[]{username});
        try {
            if (rs.next()) {
                return rs.getString("last_login");
            }
        } catch (Exception e) {
            System.out.println("Error in fetching  last_login");
        }
        return "";
    }



    public boolean checkIfAlreadyInvitedFromCurrentOrganization(String invitee){
        String query = "select invitee from invites , user_details u where inviter = u.user_id and u.orgId = ? and invitee = ?";
        ResultSet rs = executeQuery(query, new String[]{org, invitee});
        try {
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error in fetching query");
        }
        return false;
    }
    public ArrayList<String> getListOfUsersQuery() {
        String query = "select user_id, role from "+tableName+" where orgId = ? ";
        String condition = " order by case when role ='superadmin' then 1 when role='admin' then 2 when role='user' then 3 end asc";
        query += condition;
        ArrayList<String> users = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(query, new String[]{org});
            while (rs.next()) {
                String store[] = new String[2];
                store[0] = rs.getString("user_id");
                store[1] = rs.getString("role");
                String list = store[0] + ((store[0].equals(temp_object.getUserId())) ? "(You)" : "") + " Role: " + store[1];
                users.add(list);                
            }
        } catch (Exception e) {
            System.out.println("Listing error: " + e);
        }
        return users;
    }

    public String getPublicKey(String userId){
        String query = "select publicKey from user_rsakeys where userId = '"+userId+"'";
        try {
            ResultSet rs = executeQuery(query);
            if(rs.next()){
                return rs.getString("publicKey");
            }
        } catch (Exception e) {

        }
        return null;
    }

    public String getEncrytpedPrivateKey(String userId){
        String query = "select privateKey from user_rsakeys where userId = '"+userId+"'";
        try {
            ResultSet rs = executeQuery(query);
            if(rs.next()){
                return rs.getString("privateKey");
            }
        } catch (Exception e) {

        }
        return null;
    }
}
