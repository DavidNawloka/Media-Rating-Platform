package at.fhtw.swen1.repository;

import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRepository {

    public boolean find(int userId, int ratingId){
        String sql = "SELECT * FROM public.rating_likes WHERE user_id = ? AND rating_id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            stmt.setInt(2, ratingId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return true;
            }
            return false;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding like entry "+e);
        }
    }

    public void save(int loggedInUserId, int ratingId) {
        String sql = "INSERT INTO rating_likes (user_id, rating_id) VALUES (?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, ratingId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating media entry",e);
        }
    }

}
