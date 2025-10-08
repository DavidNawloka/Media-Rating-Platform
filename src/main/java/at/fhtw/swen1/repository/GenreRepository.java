package at.fhtw.swen1.repository;

import at.fhtw.swen1.model.Genre;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRepository {
    public Genre getGenre(int genreId) {
        String sql = "SELECT * FROM genres WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("name"));
                return genre;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding genre "+e);
        }

    }
}
