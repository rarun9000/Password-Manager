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

/**
 *
 * @author rarun
 */
public class UserManagementQueries extends Sql {

    UsersObject temp_object = UsersObject.getInstance();
    String org = temp_object.getOrganization();
    String tableName = "users";
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
        String query = "update users set last_login = (SELECT CURRENT_TIMESTAMP()) where user_id = ? ";// and organization ='"+org+"'";
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
    public void inviteUser(String new_user_id, String user_type, String link) {
        String query = "insert into invites(inviter,invitee,link,user_type) values( ? , ? , ? ,?)";
        updateQuery(query, new String[]{temp_object.getUserId(), new_user_id, link, user_type});
    }
    public ArrayList<String[]> myPendingInvites(){
        ArrayList<String[]> invites = new ArrayList<>();
        String query = "select invitee, invited_on,link,user_type from invites where inviter = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{temp_object.getUserId()});
            while(rs.next()) {
                String[] invite_data = new String[4];
                invite_data[0] = rs.getString("invitee");
                invite_data[1] = rs.getString("invited_on");
                invite_data[2] = rs.getString("link");
                invite_data[3] = rs.getString("user_type");
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
        String query = "delete from "+tableName+" where user_id= ? and organization = ?";
        return updateQuery(query,new String[]{user, org});
    }
    
    public String getOrganizationOfUser(String user_id){
        String query = "select organization from "+tableName+" where user_id = ? ";
        try{
            ResultSet rs = executeQuery(query, new String[]{user_id});
            if(rs.next()){
                return rs.getString("organization");
            }
        }
        catch(Exception e){
            System.out.println("getOrganization error");
        }
        return "";
    }
    public String getUserType(String username) {
        String query = "select account_type from "+tableName+" where user_id = ? ";
        ResultSet rs = executeQuery(query, new String[]{username});
        try {
            if (rs.next()) {
                return rs.getString("account_type");
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
    public int addUserQuery(String username, String password, String type) {
        String query = "insert into users(user_id,master_password,account_type,last_login,organization) values( ? , ? , ? ,? ,?)";
        return updateQuery(query, new String[]{username, password, type, null, org});
    }

    public int addUserQuery(String username, String password, String type, String org) {
        String query = "insert into users(user_id,master_password,account_type,last_login,organization) values( ? ,? ,?,?,?)";
        // accessing from invitation class
        return updateQuery(query, new String[]{username, password.hashCode()+"", type, null, org});
    }
    public boolean checkIfAlreadyInvitedFromCurrentOrganization(String invitee){
        String query = "select invitee from invites , users u where inviter = u.user_id and u.organization = ? and invitee = ?";
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
        String query = "select user_id, account_type from "+tableName+" where organization = ? ";
        String condition = " order by case when account_type ='superadmin' then 1 when account_type='admin' then 2 when account_type='user' then 3 end asc";
        query += condition;
        ArrayList<String> users = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(query, new String[]{org});
            while (rs.next()) {
                String store[] = new String[2];
                store[0] = rs.getString("user_id");
                store[1] = rs.getString("account_type");
                String list = store[0] + ((store[0].equals(temp_object.getUserId())) ? "(You)" : "") + " Role: " + store[1];
                users.add(list);                
            }
        } catch (Exception e) {
            System.out.println("Listing error: " + e);
        }
        return users;
    }
}
