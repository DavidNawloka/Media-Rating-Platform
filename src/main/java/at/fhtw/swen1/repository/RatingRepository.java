package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RatingRepository {

    public ArrayList<Rating> findByUserId(int userId){
        String sql = "SELECT * FROM ratings WHERE user_id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Rating> ratings = new ArrayList<>();

            while(rs.next()){
                Rating rating = new Rating();
                rating.setId(rs.getInt("id"));
                rating.setMediaId(rs.getInt("media_id"));
                rating.setUserId(rs.getInt("user_id"));
                rating.setStars(rs.getInt("stars"));
                rating.setComment(rs.getString("comment"));
                rating.setCommentConfirmed(rs.getBoolean("comment_confirmed"));
                ratings.add(rating);
            }
            return ratings;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding rating entry "+e);
        }
    }

    public boolean exists(int userId, int mediaId){
        String sql = "SELECT * FROM ratings WHERE user_id = ? AND media_id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            ResultSet rs = stmt.executeQuery();


            if(rs.next()){
                return true;
            }
            return false;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding rating entry "+e);
        }
    }

    public Rating findById(int ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Rating rating = new Rating();
                rating.setId(ratingId);
                rating.setMediaId(rs.getInt("media_id"));
                rating.setUserId(rs.getInt("user_id"));
                rating.setStars(rs.getInt("stars"));
                rating.setComment(rs.getString("comment"));
                rating.setCommentConfirmed(rs.getBoolean("comment_confirmed"));
                return rating;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding rating entry "+e);
        }
    }
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

    public void confirmRating(int ratingId){
        String sql = "UPDATE ratings SET comment_confirmed = 1 WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, ratingId);

            stmt.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException("Database error while updating rating entry" + e.getMessage());
        }
    }



    public Rating update(Rating rating){
        String sql = "UPDATE ratings SET comment = ?, stars = ? WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, rating.getComment());
            stmt.setInt(2, rating.getStars());
            stmt.setInt(3, rating.getId());

            stmt.executeUpdate();
            return rating;

        }catch(SQLException e){
            throw new RuntimeException("Database error while updating rating entry" + e.getMessage());
        }
    }

    public void delete(int ratingId){
        String sql = "DELETE FROM ratings where id = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, ratingId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while deleting rating entry "+e);
        }
    }


}
