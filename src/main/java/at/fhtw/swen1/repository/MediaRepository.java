package at.fhtw.swen1.repository;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MediaRepository {

    public Media findById(int mediaId) {
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

                media.setAverageScore(getAverageRating(mediaId, conn));
                return media;
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding media entry "+e);
        }
    }

    private float getAverageRating(int mediaId, Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(AVG(stars), 0) as avg_rating FROM ratings WHERE media_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return rs.getFloat("avg_rating");
            }
            return 0f;
        }
    }

    public Media save(Media media) {
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

    public Media update(Media media){
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

    public void delete(int mediaId){
        String sql = "DELETE FROM media where id = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, mediaId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while deleting media entry "+e);
        }
    }

    public ArrayList<Media> findAllWithFilters(String title, String genreId, String mediaType, String releaseYear, String ageRestriction, String rating, String sortBy) {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT media.*, r.avg_rating FROM media " +
                "LEFT JOIN media_genres ON media.id = media_genres.media_id " +
                "LEFT JOIN (SELECT media_id, AVG(stars) as avg_rating FROM ratings GROUP BY media_id) r  on media.id = r.media_id " +
                "WHERE 1=1"
        );

        ArrayList<Object> params = new ArrayList<>();

        if(title != null && !title.isEmpty()){
            sql.append(" AND LOWER(media.title) LIKE LOWER(?)");
            params.add("%" + title + "%");
        }

        if(genreId != null && !genreId.isEmpty()){
            sql.append(" AND media_genres.genre_id = ?");
            params.add(Integer.parseInt(genreId));
        }

        if (mediaType != null && !mediaType.isEmpty()) {
            sql.append(" AND media.media_type = ?");
            params.add(mediaType.toLowerCase());
        }

        if (releaseYear != null && !releaseYear.isEmpty()) {
            sql.append(" AND media.release_year = ?");
            params.add(Integer.parseInt(releaseYear));
        }

        if (ageRestriction != null && !ageRestriction.isEmpty()) {
            sql.append(" AND media.age_restriction <= ?");
            params.add(Integer.parseInt(ageRestriction));
        }

        if (rating != null && !rating.isEmpty()) {
            sql.append(" AND r.avg_rating >= ?");
            params.add(Double.parseDouble(rating));
        }

        // Add sorting
        switch (sortBy) {
            case "title" -> sql.append(" ORDER BY media.title ASC");
            case "year" -> sql.append(" ORDER BY media.release_year DESC");
            case "score" -> sql.append(" ORDER BY r.avg_rating DESC NULLS LAST");
            case null, default -> sql.append(" ORDER BY media.created_at DESC"); // Default sort
        }

        ArrayList<Media> mediaList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Media media = new Media(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        MediaType.fromLabel(resultSet.getString("media_type")),
                        resultSet.getInt("release_year"),
                        resultSet.getInt("age_restriction"),
                        null,
                        resultSet.getInt("creator_id"),
                        resultSet.getFloat("avg_rating")
                );
                mediaList.add(media);
            }

        } catch (Exception e) {
            System.err.println("Error fetching media list: " + e.getMessage());
            e.printStackTrace();
        }

        return mediaList;
    }
}
