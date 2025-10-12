package at.fhtw.swen1.repository;

import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MediaGenreRepository {
    public void addMediaGenre(int mediaId, int genreId) {
        String sql = "INSERT INTO media_genres (media_id, genre_id) VALUES (?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, mediaId);
            stmt.setInt(2, genreId);

            stmt.executeUpdate();


        }catch (SQLException e){
            throw new RuntimeException("Database error while creating media_genre entry"+e.getMessage());
        }
    }
}
