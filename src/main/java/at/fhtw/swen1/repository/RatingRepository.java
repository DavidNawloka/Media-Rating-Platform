package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingRepository {

    public Rating save(Rating rating) {
        String sql = "INSERT INTO ratings (media_id, user_id, stars, comment) VALUES (?, ?, ?, ?) RETURNING id";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, rating.getMediaId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getStars());
            stmt.setString(4, rating.getComment());

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                rating.setId(rs.getInt("id"));
                return rating;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating rating entry",e);
        }
    }
}
