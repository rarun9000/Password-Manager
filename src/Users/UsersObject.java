package Users;

import Database.UserManagementQueries;
import Encryption.*;

public class UsersObject {    
    private static String user_id;
    private static String master_password;
    private static String account_type;
    private static String last_login;
    private static String orgId;
    private static String publicKey;
    private static String privateKey;

    private UsersObject(){}
    
    private static UsersObject obj = null;
    
    public static UsersObject getInstance(){
        if(obj == null){
            obj = new UsersObject();
        }
        return obj;
    }
    
    public static UsersObject getInstance(String[] data){        
        if(obj == null)
            obj = new UsersObject();
        setData(data); 
        setRSAKeys();       
        new UserManagementQueries().updateLastLogin();
        return obj;
    }
    
    public static void setRSAKeys(){
        UserManagementQueries queries = new UserManagementQueries();
        String publicKey = queries.getPublicKey(user_id);
        String encryptedPrivateKey = queries.getEncrytpedPrivateKey(user_id);
        String privateKey = AES.decrypt(encryptedPrivateKey, master_password);
        UsersObject.privateKey = privateKey;
        UsersObject.publicKey = publicKey;
    }

    public static void setData(String[] data){
        user_id = data[0];
        master_password = data[1];
        account_type = data[2];
        last_login = data[3];
        orgId = data[4];
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
     public void setOrganization(String orgId){
        UsersObject.orgId = orgId;
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
        return orgId;
    }
    public String getPrivateKey(){
        return privateKey;
    }
    public String getPublicKey(){
        return publicKey;
    }
    public void clear(){
        user_id = null;
        master_password = null;
        account_type = null;
        last_login = null;
        orgId = null;
        privateKey = null;
        publicKey = null;
    }
    
    public void DestroyCurrentInstance(){
        clear();       
        obj = null;
    }
}
