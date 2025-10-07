package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.User;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.*;

public class UserRepository {
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, email FROM users WHERE username = ?";

        try( Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setHashedPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                return user;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user"+e);
        }
    }

    public User findById(int id){
        String sql = "SELECT username, email FROM users WHERE id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                return user;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user"+e);
        }
    }

    public User save(User user){
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?) RETURNING id";
        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getHashedPassword());
            stmt.setString(3, user.getEmail());

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                user.setId(rs.getInt("id"));
                return user;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user",e);
        }

    }
}
