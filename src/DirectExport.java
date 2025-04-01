import java.sql.*;
import java.io.*;

public class DirectExport {
    public static void main(String[] args) {
        String driver = "transbase.jdbc.Driver";
        String url = "jdbc:transbase://localhost:5024/sas_saabdb";
        String user = "tbadmin";
        String password = "dgs";
        String table = "sas_saabdb";
        String outputFile = table + "_export.csv";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Load JDBC driver
            System.out.println("Loading JDBC driver...");
            Class.forName(driver);
            
            // Connect to database
            System.out.println("Connecting to database: " + url);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected successfully!");
            
            // First, let's get a list of tables to confirm the table exists
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("\nAvailable tables:");
            System.out.println("----------------");
            
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            boolean tableFound = false;
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
                if (tableName.equalsIgnoreCase(table)) {
                    tableFound = true;
                }
            }
            
            if (!tableFound) {
                System.out.println("\nWARNING: Table '" + table + "' not found in database!");
                System.out.println("Please check table name or database connection.");
                return;
            }
            
            // Execute query
            System.out.println("\nExporting table: " + table);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table);
            
            // Get metadata
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            // Create CSV file
            FileWriter writer = new FileWriter(outputFile);
            
            // Write header
            for (int i = 1; i <= columnCount; i++) {
                writer.append(rsMetaData.getColumnName(i));
                if (i < columnCount) {
                    writer.append(",");
                }
            }
            writer.append("\n");
            
            // Write data
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value != null) {
                        // Escape quotes and commas in values
                        value = value.replace("\"", "\"\"");
                        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                            writer.append("\"").append(value).append("\"");
                        } else {
                            writer.append(value);
                        }
                    }
                    
                    if (i < columnCount) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
                
                // Show progress
                if (rowCount % 100 == 0) {
                    System.out.println("Processed " + rowCount + " rows...");
                }
            }
            
            writer.flush();
            writer.close();
            System.out.println("\nExport completed successfully!");
            System.out.println("Exported " + rowCount + " rows to " + outputFile);
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 