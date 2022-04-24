/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import IOClasses.MenuHandler;
import Users.UsersObject;
import java.sql.ResultSet;
import java.util.*;

/**
 *
 * @author rarun
 */
public class PasswordManagementQueries extends Sql {

    UsersObject uo = UsersObject.getInstance();
    String current_user = uo.getUserId();
    
    String tableSharedAccount = "shared_accounts";
    String tableAccounts = "accounts";
    
    public int updateAccountDetails(String name, String url, String username, String password, String res_id) {
        String nameAddon = (name != null) ? "name = ?" : "";
        //add , before url if name is not null
        String urlAddon = ((url != null) ? ((name != null) ? "," : "") + "url = ?" : "");
        //add , before username if name is not null or url is not null
        String usernameAddon = ((username != null) ? ((name != null || url != null) ? "," : "") + "username = ?" : "");
        //add , before username if name is not null or url is not null or username is not null
        String passwordAddon = ((password != null) ? ((name != null || url != null || username != null) ? "," : "") + "password = ?" : "");

        String query = "update "+tableAccounts+" set " + nameAddon + urlAddon + usernameAddon + passwordAddon + " where res_id  = ?";
        ArrayList<String> updates = new ArrayList<>();
        if(name != null) updates.add(name);
        if(url != null) updates.add(url);
        if(username != null) updates.add(username);
        if(password != null) updates.add(password);        
        updates.add(res_id);
        return updateQuery(query, new MenuHandler().arrayListToStringArrayConverter(updates));
    }

    //[name -> password]
    public int revokeAccessOfAllAcccountsSharedByTheUser(String user) {
        String query = "delete from "+tableSharedAccount+" where res_id in (select res_id from (select s.res_id from "+tableAccounts+" a,"+tableSharedAccount+" s where a.res_id = s.res_id and owner = ? ) as C)";
        return updateQuery(query, new String[]{user});
    }

    public void getDetailsOfAllAccountsSharedByMeTo(String res_id, String sharee) {
       // String query = "select * from "+tableAccounts+" a,"+tableSharedAccount+" s where a.res_id = s.res_id and owner = '" + current_user + "' and sharee='" + sharee + "' and s.res_id='" + res_id + "'";
    }

    public void revokeAccessOfAccountToUser(String res_id, String sharee) {
        String query = "delete from "+tableSharedAccount+" where res_id = ? and sharee= ?";
        updateQuery(query, new String[]{res_id, sharee});
    }

    public int deleteAccountsCreatedByTheUser(String user) {
        String query = "delete from "+tableAccounts+" where owner= ?";
        return updateQuery(query, new String[]{user});
    }

    public int deleteAccount(String res_id) {
        String query = "delete from "+tableAccounts+" where res_id= ?";
        return updateQuery(query, new String[]{res_id});
    }

    public int revokeAccessOfThisAccountToAllOtherUsers(String res_id) {
        String query = "delete from "+tableSharedAccount+" where res_id= ?";
        return updateQuery(query, new String[]{res_id});
    }

    public int revokeAccessOfAllAccountsSharedToTheUser(String user) {
        String query = "delete from "+tableSharedAccount+" where sharee= ?";
        return updateQuery(query, new String[]{user});
    }

    public ArrayList<String> listOfShareeOfAnAccount(String res_id) {
        ArrayList<String> sharee = new ArrayList<>();
        try {
            String query = "select sharee from "+tableAccounts+" a,"+tableSharedAccount+" s where a.res_id = s.res_id and owner = ? and a.res_id= ? ";
            ResultSet rs = executeQuery(query, new String[]{current_user, res_id});
            while (rs.next()) {
                sharee.add(rs.getString("sharee"));
            }
        } catch (Exception e) {
            System.out.println("Error in fetching sharee");
        }
        return sharee;
    }

    public ArrayList<String[]> getAccountDetailsCreatedByMe() {
        ArrayList<String[]> account_details = new ArrayList<>();

        String query = "select name,url,username,password,res_id from "+tableAccounts+" where owner= ?";
        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String account_detail[] = new String[5];
                account_detail[0] = rs.getString("name") + " (C)";
                account_detail[1] = rs.getString("url");
                account_detail[2] = rs.getString("username");
                account_detail[3] = rs.getString("password");
                account_detail[4] = rs.getString("res_id");
                account_details.add(account_detail);
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details" + e);
        }
        return account_details;
    }

    public String[] getAccountDetail(String res_id) {
        String account_details[] = new String[5];
        String query = "select * from "+tableAccounts+" where res_id= ?";
        ResultSet rs = executeQuery(query, new String[]{res_id});
        try {
            if (rs.next()) {
                account_details[0] = rs.getString("name");
                account_details[1] = rs.getString("url");
                account_details[2] = rs.getString("username");
                account_details[3] = rs.getString("password");
                account_details[4] = rs.getString("res_id");
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details: " + e);
        }
        return account_details;
    }

    public ArrayList<String[]> getSharedToMeAccountDetails() {
        ArrayList<String[]> account_detail = new ArrayList<>();
        String query = "select name,url,username,password,s.res_id,owner from "+tableSharedAccount+" s, "+tableAccounts+" a where s.res_id = a.res_id and s.sharee = ?";
        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String[] account_details = new String[6];
                account_details[0] = rs.getString("name") + " (S)";
                account_details[1] = rs.getString("url");
                account_details[2] = rs.getString("username");
                account_details[3] = rs.getString("password");
                account_details[4] = rs.getString("res_id");
                account_details[5] = rs.getString("owner");
                account_detail.add(account_details);
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details: " + e);
        }
        return account_detail;
    }

    public ArrayList<String[]> getSharedByMeAccountDetails() {
        ArrayList<String[]> account_details = new ArrayList<>();
        String query = "select name,url,username,password,s.res_id,owner from "+tableSharedAccount+" s, "+tableAccounts+" a where s.res_id = a.res_id and a.owner= ? ";

        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String[] account_detail = new String[6];
                account_detail[0] = rs.getString("name");
                account_detail[1] = rs.getString("url");
                account_detail[2] = rs.getString("username");
                account_detail[3] = rs.getString("password");
                account_detail[4] = rs.getString("res_id");
                account_detail[5] = rs.getString("owner");
                account_details.add(account_detail);
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details: " + e);
        }
        return account_details;
    }

    public boolean checkIfAlreadyExists(String account_name) {
        String query = "select * from "+tableAccounts+" where owner = ? and name = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{current_user, account_name});
            if (rs.next()) {
                System.out.println("Account with the same name already exists!");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error in CheckIfAlreadyExist() function" + e);
        }
        return false;
    }

    public void addPassword(String name, String username, String url, String password, String res_id) {
        String query = "insert into "+tableAccounts+"(owner,name,url,username,password,res_id) values( ? ,? , ? ,? , ? ,?)";
        updateQuery(query, new String[]{current_user, name, url, username, password, res_id});
    }

    public void shareAccount(String sharee, String res_id) {
        String query = "insert into "+tableSharedAccount+"(sharee,res_id) values(? ,?)";
        updateQuery(query, new String[]{sharee, res_id});
    }

    public boolean checkIfAlreadyShared(String sharee, String res_id) {
        String query = "Select * from "+tableSharedAccount+" where sharee = ? and res_id = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{sharee, res_id});
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error in checking if already shared");
            System.exit(0);
        }
        return false;
    }
}
