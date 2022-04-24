package Database;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;

/**
 *
 * @author rarun
 */
public class Sql {

    private static Connection conn;
    private Statement st;
    private String database="passwordmanager";
    private PreparedStatement ps;

    Sql() {
        createConnection();
    }

    void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database, "root", "root");
        } catch (Exception e) {
            System.out.println("SQL: Connection Error!"+ e);
            System.exit(0);
        }
    }

    public void getStatement() {
        try {
            st = conn.createStatement();
        } catch (Exception e) {
            System.out.println("Statement creation error: ");
            System.exit(0);
        }
    }

    public void getPreparedStatement(String query) {
        try {
            ps = conn.prepareStatement(query);
        } catch (Exception e) {
            System.out.println("Prepared Statement creation error: ");
            System.exit(0);
        }
    }

    public int updateQuery(String query) {
        getStatement();
        try {
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Update Query Error: " + e);
            System.exit(0);
        }
        return 1;
    }

    public ResultSet executeQuery(String query) {
        getStatement();
        ResultSet rs = null;
        try {
            rs = st.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Execute Query Error: " + e);
            System.exit(0);
        }
        return rs;
    }

    public int updateQuery(String query, String[] values) {
        getPreparedStatement(query);
        try {
            for (int i = 1; i <= values.length; i++) {
                ps.setString(i, values[i - 1]);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("PreparedStatement Update Query Error: " + e);
            System.exit(0);
        }
        return 1;
    }

    public ResultSet executeQuery(String query, String[] values) {
        ResultSet rs = null;
        getPreparedStatement(query);
        try {
            for (int i = 1; i <= values.length; i++) {
                if (values[i - 1] == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setString(i, values[i - 1]);
                }
            }
            rs = ps.executeQuery();
        } catch (Exception e) {
            System.out.println("PreparedStatement Execute Query Error: " + e);
            System.exit(0);
        }
        return rs;
    }

    void close() {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection closing error");
            System.exit(0);
        }
    }
}
