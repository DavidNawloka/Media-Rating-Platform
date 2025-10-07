package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.*;

public class SessionRepository {
    public void createSession(Session session) {
        String sql = "INSERT INTO sessions (token,userId,expiresAt) VALUES (?, ?, ?) ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, session.getToken());
            stmt.setInt(2, session.getUserId());
            stmt.setTimestamp(3, session.getExpiresAt());

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating session"+e);
        }

    }

    public void deleteSession(String token) {
    }

    public Session getSession(String userId) {
        return null;
    }
}
