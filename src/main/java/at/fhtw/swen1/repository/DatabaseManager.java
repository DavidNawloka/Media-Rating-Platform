package at.fhtw.swen1.repository;

import at.fhtw.swen1.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "MRP");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "admin");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "admin");

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