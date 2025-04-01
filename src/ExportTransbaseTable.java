import java.sql.*;
import java.io.*;

public class ExportTransbaseTable {
    public static void main(String[] args) {
        String jdbcDriver = "transbase.jdbc.Driver";
        String dbUrl = "jdbc:transbase://localhost:5024/sas_saabdb";
        String username = "tbadmin";
        String password = "gds";
        String tableName = "sas_saabdb";
        String csvFile = "sas_saabdb_export.csv";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Register JDBC driver
            Class.forName(jdbcDriver);
            
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(dbUrl, username, password);
            
            // Execute a query
            System.out.println("Exporting table: " + tableName);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            
            // Get column count
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Create CSV file
            PrintWriter writer = new PrintWriter(new File(csvFile));
            StringBuilder sb = new StringBuilder();
            
            // Write header
            for (int i = 1; i <= columnCount; i++) {
                sb.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    sb.append(",");
                }
            }
            sb.append("\n");
            writer.write(sb.toString());
            
            // Write data rows
            while (rs.next()) {
                sb = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    // Escape quotes and handle nulls
                    if (value == null) {
                        sb.append("");
                    } else {
                        value = value.replace("\"", "\"\"");
                        sb.append("\"").append(value).append("\"");
                    }
                    
                    if (i < columnCount) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
                writer.write(sb.toString());
            }
            
            writer.close();
            System.out.println("Data exported successfully to " + csvFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
} 