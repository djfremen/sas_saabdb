import java.sql.*;

public class ListTransbaseTables {
    public static void main(String[] args) {
        String jdbcDriver = "transbase.jdbc.Driver";
        String dbUrl = "jdbc:transbase://localhost:5024/storage";
        String username = "tbadmin";
        String password = "gds";
        
        Connection conn = null;
        
        try {
            // Register JDBC driver
            Class.forName(jdbcDriver);
            
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connected successfully!");
            
            // Get database metadata
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Get tables
            System.out.println("\nAvailable tables:");
            System.out.println("----------------");
            
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close connection
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
} 