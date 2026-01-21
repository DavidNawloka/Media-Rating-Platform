package at.fhtw.swen1.repository;

import at.fhtw.swen1.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    private static final String HOST = getConfig("DB_HOST", "localhost");
    private static final String PORT = getConfig("DB_PORT", "5432");
    private static final String DB_NAME = getConfig("DB_NAME", "MRP");
    private static final String USER = getConfig("DB_USER", "admin");
    private static final String PASSWORD = getConfig("DB_PASSWORD", "admin");

    private static String getConfig(String key, String defaultValue){
        String property = System.getProperty(key); // In testing System is used, in docker compose env is used
        if(property != null) return property;
        return System.getenv().getOrDefault(key,defaultValue);
    }

    private static final String URL = String.format(
            "jdbc:postgresql://%s:%s/%s?currentSchema=public",
            HOST, PORT, DB_NAME
    );

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to connect to database", e);
        }
    }
}