import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExportAllTables {
    public static void main(String[] args) {
        String driver = "transbase.jdbc.Driver";
        String url = "jdbc:transbase://localhost:5024/sas_saabdb";
        String user = "tbadmin";
        String password = "dgs";
        
        Connection conn = null;
        
        try {
            // Load JDBC driver
            System.out.println("Loading JDBC driver...");
            Class.forName(driver);
            
            // Connect to database
            System.out.println("Connecting to database: " + url);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected successfully!");
            
            // Get list of tables
            List<String> tables = new ArrayList<>();
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("\nAvailable tables:");
            System.out.println("----------------");
            
            ResultSet tablesList = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tablesList.next()) {
                String tableName = tablesList.getString("TABLE_NAME");
                System.out.println(tableName);
                tables.add(tableName);
            }
            
            // Export each table
            for (String tableName : tables) {
                exportTable(conn, tableName);
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void exportTable(Connection conn, String tableName) {
        Statement stmt = null;
        ResultSet rs = null;
        String outputFile = tableName + "_export.csv";
        
        try {
            // Execute query
            System.out.println("\nExporting table: " + tableName);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            
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
            System.out.println("Export completed successfully!");
            System.out.println("Exported " + rowCount + " rows to " + outputFile);
            
        } catch (Exception e) {
            System.out.println("ERROR exporting table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 