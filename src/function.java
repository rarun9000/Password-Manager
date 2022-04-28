import Encryption.*;


public class function {
    public static void main(String[] args) {
        RSAKeyPairGenerator gen = new RSAKeyPairGenerator();
        String privatek = gen.getPrivateKey();
        String publick = gen.getPublicKey();
        // System.out.println(privatek + "\n" + publick);
        try {
            // byte[] encrypt = rsa.encrypt("Hello World", publick);
            // String encrypt_string = new String(Base64.getEncoder().encode(encrypt));
            // encrypt = Base64.getDecoder().decode(encrypt_string);

            String encrypt = RSAUtil.getEncryptedString("", publick);
            String decrypt = RSAUtil.decrypt(encrypt, privatek);
            System.out.println(encrypt);
            System.out.println(decrypt);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
