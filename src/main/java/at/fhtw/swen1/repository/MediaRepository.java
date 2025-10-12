package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaRepository {
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
}
