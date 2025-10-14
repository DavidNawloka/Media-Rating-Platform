package at.fhtw.swen1.repository;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaRepository {

    public Media findyById(int mediaId) {
        String sql = "SELECT * FROM media WHERE id = ?";

        try( Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Media media = new Media();
                media.setId(mediaId);
                media.setTitle(rs.getString("title"));
                media.setDescription(rs.getString("description"));
                media.setAgeRestriction(rs.getInt("age_restriction"));
                media.setReleaseYear(rs.getInt("release_year"));
                media.setMediaType(MediaType.fromLabel(rs.getString("media_type")));
                media.setCreatorId(rs.getInt("creator_id"));
                return media;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding media entry "+e);
        }
    }

    public Media createMedia(Media media) {
        String sql = "INSERT INTO media (title, description, media_type, release_year, age_restriction, creator_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType().label);
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setInt(6, media.getCreatorId());

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                media.setId(rs.getInt("id"));
                return media;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating media entry",e);
        }
    }

    public Media updateMedia(Media media){
        String sql = "UPDATE Media SET title = ?, description = ?, media_type = ?, release_year = ?, age_restriction = ? WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType().label);
            stmt.setInt(4, media.getReleaseYear());
            stmt.setInt(5, media.getAgeRestriction());
            stmt.setInt(6, media.getId());

            stmt.executeUpdate();
            return media;

        }catch(SQLException e){
            throw new RuntimeException("Database error while updating media entry" + e.getMessage());
        }
    }

    public void deleteMedia(int mediaId){
        String sql = "DELETE FROM media where id = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, mediaId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while deleting media entry "+e);
        }
    }
}
