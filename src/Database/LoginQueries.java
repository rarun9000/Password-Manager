/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.*;

public class LoginQueries extends Sql{
    public LoginQueries(){
        
    }
    public LoginQueries(String user){
        fetchCredentials(user);
    }

    /*
        List of functions
            1. GetResultSet()
            2. isValidUserId()
            3. getRS()
            4. getHashPass()
    */
    String tableName= "user_credentials";
    String tableName1= "user_details";


    private boolean isValidUser = false;
    private String hashpass=new String();
    
    public void fetchCredentials(String user_id){
        String query = "Select userId, masterPassword from "+tableName1+" ud , "+tableName+" where ud.user_id = ?";
        ResultSet rs = executeQuery(query, new String[]{user_id});
        try{
            if(rs.next()){
                isValidUser = true;
                hashpass = rs.getString("masterPassword");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }    

    public boolean isValidUser(){
        return isValidUser;
    }
    public String getHashPass(){
        return hashpass;
    }
    
    public boolean isValidUserId(String user){
        String query = "Select * from user_details where user_id = ?";
        ResultSet rs = executeQuery(query,new String[]{user});
        try{
            if(rs.next()){                
                return true;
            }
        }
        catch(Exception e){
            System.out.println("Error in valid user check : "+e);
        }
        return false;
    }
    
    public String getHashPass(String user){
        String query = "Select * from "+tableName+" where userId = ?";
        ResultSet rs = executeQuery(query,new String[]{user});
        try{
            if(rs.next()){                
                return rs.getString("masterPassword");
            }
        }
        catch(Exception e){
            System.out.println("Error in valid user check : "+e);
        }
        return "";
    }
}
