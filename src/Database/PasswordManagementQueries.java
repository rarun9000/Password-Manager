/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import IOClasses.MenuHandler;
import Management.*;
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
    String tableAccounts = "password_accounts";
    
    public int updateAccountDetails(String name, String username, String encryptedPassword, String resId) {
        String nameAddon = (name != null) ? "accountName = ?" : "";
        //add , before username if name is not null 
        String usernameAddon = ((username != null) ? ((name != null ) ? "," : "") + "username = ?" : "");
        //add , before username if name is not null or username is not null
        String passwordAddon = ((encryptedPassword != null) ? ((name != null  || username != null) ? "," : "") + "encryptedPassword = ?" : "");

        String query = "update "+tableAccounts+" set " + nameAddon + usernameAddon + passwordAddon + " where resId  = ?";
        ArrayList<String> updates = new ArrayList<>();
        if(name != null) updates.add(name);
        if(username != null) updates.add(username);
        if(encryptedPassword != null) updates.add(encryptedPassword);        
        updates.add(resId);
        return updateQuery(query, new MenuHandler().arrayListToStringArrayConverter(updates));
    }

    //[name -> password]
    public int revokeAccessOfAllAcccountsSharedByTheUser(String user) {
        String query = "delete from "+tableSharedAccount+" where resId in (select resId from (select s.resId from "+tableAccounts+" a,"+tableSharedAccount+" s where a.resId = s.resId and owner = ? ) as C)";
        return updateQuery(query, new String[]{user});
    }

    public void getDetailsOfAllAccountsSharedByMeTo(String resId, String sharee) {
       // String query = "select * from "+tableAccounts+" a,"+tableSharedAccount+" s where a.resId = s.resId and owner = '" + current_user + "' and sharee='" + sharee + "' and s.resId='" + resId + "'";
    }

    public void revokeAccessOfAccountToUser(String resId, String sharee) {
        String query = "delete from "+tableSharedAccount+" where resId = ? and sharee= ?";
        updateQuery(query, new String[]{resId, sharee});
    }

    public int deleteAccountsCreatedByTheUser(String user) {
        String query = "delete from "+tableAccounts+" where owner= ?";
        return updateQuery(query, new String[]{user});
    }

    public int deleteAccount(String resId) {
        String query = "delete from "+tableAccounts+" where resId= ?";
        return updateQuery(query, new String[]{resId});
    }
    public int deleteAccount(ArrayList<String> accounts) {
        String query = "delete from "+tableAccounts+" where resId IN " + new PasswordManagement().ArrayListToStringQuery(accounts);
        return updateQuery(query);
    }
    public int revokeAccessOfThisAccountToAllOtherUsers(String resId) {
        String query = "delete from "+tableSharedAccount+" where resId= ?";
        return updateQuery(query, new String[]{resId});
    }

    public int revokeAccessOfAllAccountsSharedToTheUser(String user) {
        String query = "delete from "+tableSharedAccount+" where sharee= ?";
        return updateQuery(query, new String[]{user});
    }

    public ArrayList<String> listOfShareeOfAnAccount(String resId) {
        ArrayList<String> sharee = new ArrayList<>();
        try {
            String query = "select sharee from "+tableAccounts+" a,"+tableSharedAccount+" s where a.resId = s.resId and owner = ? and a.resId= ? ";
            ResultSet rs = executeQuery(query, new String[]{current_user, resId});
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

        String query = "select accountName,username,encryptedPassword,resId from "+tableAccounts+" where owner= ?";
        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String account_detail[] = new String[5];
                account_detail[0] = rs.getString("accountName") + " (C)";
                account_detail[1] = rs.getString("username");
                account_detail[2] = rs.getString("encryptedPassword");
                account_detail[3] = rs.getString("resId");
                account_detail[4] = getEncryptedKeyOfPasswordAccount(account_detail[3]);
                account_details.add(account_detail);
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details" + e);
        }
        return account_details;
    }

    public String getEncryptedKeyOfPasswordAccount(String resId){
        String query = "select encryptedKey from password_keys where resId='"+resId+"'";
        try{
            ResultSet rs = executeQuery(query);
            if(rs.next()){
                return rs.getString("encryptedKey");
            }
        }
        catch(Exception e){}
        return null;
    }

    public String[] getAccountDetail(String resId) {
        String account_details[] = new String[5];
        String query = "select * from "+tableAccounts+" where resId= ?";
        ResultSet rs = executeQuery(query, new String[]{resId});
        try {
            if (rs.next()) {
                account_details[0] = rs.getString("accountName");
                account_details[1] = rs.getString("username");
                account_details[2] = rs.getString("encryptedPassword");
                account_details[3] = rs.getString("resId");
                account_details[4] = rs.getString("encryptedKey");
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details: " + e);
        }
        return account_details;
    }

    public ArrayList<String[]> getSharedToMeAccountDetails() {
        ArrayList<String[]> account_detail = new ArrayList<>();
        String query = "select accountName,username,encryptedPassword,s.resId,owner,encryptedKey from "+tableSharedAccount+" s, "+tableAccounts+" a where s.resId = a.resId and sharee = ?";
        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String[] account_details = new String[6];
                account_details[0] = rs.getString("accountname") + " (S)";
                account_details[1] = rs.getString("username");
                account_details[2] = rs.getString("encryptedPassword");
                account_details[3] = rs.getString("resId");
                account_details[4] = rs.getString("encryptedKey");
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
        String query = "select accountName,username,encryptedPassword,s.resId from "+tableSharedAccount+" s, "+tableAccounts+" a where s.resId = a.resId and a.owner= ? ";

        ResultSet rs = executeQuery(query, new String[]{current_user});
        try {
            while (rs.next()) {
                String[] account_detail = new String[5];
                account_detail[0] = rs.getString("accountName");
                account_detail[1] = rs.getString("username");
                account_detail[2] = rs.getString("encryptedPassword");
                account_detail[3] = rs.getString("resId");
                account_detail[4] = this.getEncryptedKeyOfPasswordAccount(account_detail[3]);
                account_details.add(account_detail);
            }
        } catch (Exception e) {
            System.out.println("Error in fetching account details: " + e);
        }
        return account_details;
    }

    public boolean checkIfAlreadyExists(String account_name) {
        String query = "select * from "+tableAccounts+" where owner = ? and accountName = ?";
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

    public void addPassword(String name, String username, String encryptedPassword, String resId, String encryptedKey) {
        String query = "insert into "+tableAccounts+"(owner,accountName,username,encryptedPassword,resId) values( ? , ? ,? , ? ,?)";
        updateQuery(query, new String[]{current_user, name, username, encryptedPassword, resId});
        addKey(resId, encryptedKey);
    }

    public void addKey(String resId, String encryptedKey){
        String query = "insert into password_keys(resId, encryptedKey) values(?,?)";
        updateQuery(query, new String[]{resId, encryptedKey});
    }

    public void shareAccount(String sharee, String resId, String encryptedKey) {
        String query = "insert into "+tableSharedAccount+"(sharee, resId, encryptedKey) values(? ,?, ?)";
        System.out.println(query+ sharee+"\t"+resId+"\t"+encryptedKey);
        updateQuery(query, new String[]{sharee, resId, encryptedKey});
    }

    public boolean checkIfAlreadyShared(String sharee, String resId) {
        String query = "Select * from "+tableSharedAccount+" where sharee = ? and resId = ?";
        try {
            ResultSet rs = executeQuery(query, new String[]{sharee, resId});
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
