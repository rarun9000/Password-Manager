package Encryption;

//import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class RSAKeyPairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAKeyPairGenerator(){
        try{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        }
        catch(Exception e){}
    }

    public String getPrivateKey() {
        return new String(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }

    public String getPublicKey() {
        return new String(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }
}
