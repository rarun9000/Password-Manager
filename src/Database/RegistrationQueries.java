/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;


import Encryption.AES;
import Encryption.RSAKeyPairGenerator;
import Management.PasswordManagement;

/**
 *
 * @author rarun
 */
public class RegistrationQueries extends Sql {
    /*
     * List of functions
     * 1. AddOrganization()
     * 2. isValidOrganization()
     */
    String tableName = "users";
    String orgTable = "organization_details";

    public String addOrganizationAndUser(String username, String password, String org_name) {
        String orgId = new PasswordManagement().uniqueIDGenerator();
        if (createOrganization(org_name, orgId) == 0 || createUser(username, null , "superadmin", org_name, password) == 0) {
            deleteOrganization(orgId);
            System.out.println("Registration UnSuccessful!");
            throw new RuntimeException("Reistration Failed");
        }
        System.out.println("Registration Successful!");
        return orgId;
    }

    
    public int createUser(String username , String name ,String role, String org_id, String masterPassword){
        String query = "insert into user_details(user_id ,name, role, orgId) values(?,?,?,?)";
        int userCreation = updateQuery(query, new String[] { username,name,role, org_id });
        int credentialsCreation = createCredentials(username, masterPassword);
        int keyCreation = addKeys(username , masterPassword);

        return (userCreation==1 && credentialsCreation==1 && keyCreation==1) == true ? 1 : 0;
    }

    public int createCredentials(String username, String password) {
        String query = "insert into user_credentials(userId, masterPassword) values(?, ?)";
        return updateQuery(query, new String[] { username, password.hashCode() + "" });
    }

    public int createOrganization(String orgName, String orgId) {
        String query = "insert into " + orgTable + "(orgId, orgName) values(?,?)";
        return updateQuery(query, new String[] { orgId, orgName });
    }

    public int deleteOrganization(String orgId){
        String query = "delete from "+orgTable+" where orgId= '"+orgId+"'";
        return updateQuery(query);
    }

    public int addKeys(String userId, String masterPassword){
        RSAKeyPairGenerator gen  = new RSAKeyPairGenerator();
        String publicK = gen.getPublicKey();
        String originalPrivatekey = gen.getPrivateKey();
        String privateK = AES.encrypt( originalPrivatekey , masterPassword)  ;
    
        String query = "insert into user_rsakeys(userId, publicKey, privateKey) values(?, ?, ?)";
        return updateQuery(query, new String[]{userId, publicK,  privateK});
    }
}
