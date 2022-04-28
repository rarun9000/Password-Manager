package Encryption;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSA
{
    public static void main(String[] args) throws NoSuchAlgorithmException{
        RSAKeyPairGenerator user1_key = new RSAKeyPairGenerator();
        RSAKeyPairGenerator user2_key = new RSAKeyPairGenerator();
        
        System.out.println("User 1: private key: "+user1_key.getPrivateKey());
        System.out.println("User 1: public key: "+user1_key.getPublicKey());
        
        
        System.out.println("User 2: private key: "+user2_key.getPrivateKey());
        System.out.println("User 2: public key: "+user2_key.getPublicKey());
        
        RSAUtil rsa= new RSAUtil();
        String pass = "!mPr@dee[";
        System.out.println("Send the password: '"+pass+"' from user1 to user2");
        //use public key of user2 
        try{
        byte[] encrypt = rsa.encrypt(pass, user2_key.getPublicKey());
        
        System.out.println("Encrpted: "+new String(encrypt,StandardCharsets.UTF_8));
     //   System.out.println("Decrpted: "+rsa.decrypt( new String(encrypt,StandardCharsets.UTF_8), user2_key.getPrivateKey()));
        
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}