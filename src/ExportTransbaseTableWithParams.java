import java.sql.*;
import java.io.*;

public class ExportTransbaseTableWithParams {
    public static void main(String[] args) {
        // Default settings
        String jdbcDriver = "transbase.jdbc.Driver";
        String host = "localhost";
        String port = "5024";
        String database = "sas_saabdb";
        String dbUrl = "jdbc:transbase://" + host + ":" + port + "/" + database;
        String username = "tbadmin";
        String password = "gds";
        String tableName = "sas_saabdb";
        String csvFile = tableName + "_export.csv";
        
        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && i+1 < args.length) {
                host = args[++i];
                dbUrl = "jdbc:transbase://" + host + ":" + port + "/" + database;
            } else if (args[i].equals("-port") && i+1 < args.length) {
                port = args[++i];
                dbUrl = "jdbc:transbase://" + host + ":" + port + "/" + database;
            } else if (args[i].equals("-db") && i+1 < args.length) {
                database = args[++i];
                dbUrl = "jdbc:transbase://" + host + ":" + port + "/" + database;
            } else if (args[i].equals("-user") && i+1 < args.length) {
                username = args[++i];
            } else if (args[i].equals("-pass") && i+1 < args.length) {
                password = args[++i];
            } else if (args[i].equals("-table") && i+1 < args.length) {
                tableName = args[++i];
                csvFile = tableName + "_export.csv";
            } else if (args[i].equals("-output") && i+1 < args.length) {
                csvFile = args[++i];
            }
        }
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Register JDBC driver
            Class.forName(jdbcDriver);
            
            // Open a connection
            System.out.println("Connecting to database: " + dbUrl);
            System.out.println("Username: " + username);
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
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
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
                
                // Print progress for large tables
                if (rowCount % 1000 == 0) {
                    System.out.println("Exported " + rowCount + " rows...");
                }
            }
            
            writer.close();
            System.out.println("Data export complete. Total " + rowCount + " rows exported to " + csvFile);
            
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