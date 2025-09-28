package at.fhtw.swen1.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {

    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "MRP");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "admin");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "admin");

    private static final String URL = String.format(
            "jdbc:postgresql://%s:%s/%s?currentSchema=public",
            HOST, PORT, DB_NAME
    );
    public static Connection getConnection() throws SQLException {
        int maxRetries = 10;
        int delay = 3000; // 3 seconds between retries

        for (int i = 0; i < maxRetries; i++) {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                if (i == maxRetries - 1) {
                    System.err.println("Database connection failed after " + maxRetries + " attempts:");
                    System.err.println("URL: " + URL);
                    System.err.println("User: " + USER);
                    System.err.println("Full error: " + e.getClass().getName() + ": " + e.getMessage());
                    if (e.getCause() != null) {
                        System.err.println("Caused by: " + e.getCause().getMessage());
                    }
                    throw e;
                }

                System.out.println("Database connection attempt " + (i + 1) + " failed. Retrying in " + (delay/1000) + " seconds...");
                System.out.println("Error: " + e.getClass().getName() + ": " + e.getMessage());
                // ... rest of retry logic
            }
        }
        return null; // Should never reach here
    }

}
