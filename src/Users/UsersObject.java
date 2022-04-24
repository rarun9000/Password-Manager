package Users;

import Database.UserManagementQueries;

public class UsersObject {    
    private static String user_id;
    private static String master_password;
    private static String account_type;
    private static String last_login;
    private static String organization;
    
    private UsersObject(){}
    
    private static UsersObject obj = null;
    
    public static UsersObject getInstance(){
        if(obj == null)
            obj = new UsersObject();
        return obj;
    }
    
    public static UsersObject getInstance(String[] data){        
        if(obj == null)
            obj = new UsersObject();
        setData(data);        
        new UserManagementQueries().updateLastLogin();
        return obj;
    }
    
    public static void setData(String[] data){
        user_id = data[0];
        master_password = data[1];
        account_type = data[2];
        last_login = data[3];
        organization = data[4];
    }
    
    public void setUserId(String user_id){
        UsersObject.user_id = user_id;
    }    
    public void setMasterPassword(String pass){
        UsersObject.master_password = pass;
    }  
    public void setAccountType(String type){
        UsersObject.account_type = type;
    }
    public void setLastLogin(String last_login){
        UsersObject.last_login  = last_login;
    }
     public void setOrganization(String org){
        UsersObject.organization = org;
    }
    
    //getter methods
    public String getUserId(){
        return user_id;
    }    
    public String getMasterPassword(){
        return master_password;
    }    
    public String getAccountType(){
        return account_type;
    }
    public String getLastLogin(){
        return last_login;
    }
    public String getOrganization(){
        return organization;
    }
    
    public void clear(){
        user_id = null;
        master_password = null;
        account_type = null;
        last_login = null;
        organization = null;
    }
    
    public void DestroyCurrentInstance(){
        clear();       
        obj = null;
    }
}
