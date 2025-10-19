package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.*;

public class SessionRepository {
    public void save(Session session) {
        String sql = "INSERT INTO sessions (token,user_id,expires_at) VALUES (?, ?, ?) ";
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

    public void delete(String token) {
        String sql = "DELETE FROM sessions where token = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, token);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while deleting session"+e);
        }
    }

    public Session getSession(String token) {

        String sql = "SELECT user_id,expires_at from sessions where token = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, token);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Session session = new Session(token, rs.getInt("user_id"), rs.getTimestamp("expires_at"));
                String test;
                return session;
            }

        }catch (SQLException e){
            throw new RuntimeException("Database error while getting session"+e);
        }

        return null;
    }
}
