package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.User;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.*;

public class UserRepository {
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, email, favorite_genre_id FROM users WHERE username = ?";

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
                user.setFavoriteGenreId(rs.getInt("favorite_genre_id"));
                return user;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user"+e);
        }
    }
    public User findByEmail(String email) {
        String sql = "SELECT id, username, password, email, favorite_genre_id FROM users WHERE email = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setHashedPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFavoriteGenreId(rs.getInt("favorite_genre_id"));
                return user;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user"+e);
        }
    }


    public User findById(int id){
        String sql = "SELECT username, email, favorite_genre_id FROM users WHERE id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFavoriteGenreId(rs.getInt("favorite_genre_id"));
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

    public User update(String username, String email, Integer favoriteGenreId, int userId){
        String sql = "UPDATE users SET username = ?, email = ?, favorite_genre_id = ? WHERE id = ?";
        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, email);
            if(favoriteGenreId == null){
                stmt.setNull(3, Types.INTEGER);
            }else{
                stmt.setInt(3, favoriteGenreId);
            }
            stmt.setInt(4, userId);

            stmt.executeUpdate();

            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setFavoriteGenreId(favoriteGenreId);
            return user;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding user",e);
        }
    }
}
