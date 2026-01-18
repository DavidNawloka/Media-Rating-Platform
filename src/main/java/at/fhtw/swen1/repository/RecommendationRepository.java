package at.fhtw.swen1.repository;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecommendationRepository {

    public ArrayList<Integer> getRatedMediaIds(int userId){
        String sql = "SELECT media_id FROM ratings where user_id=?";
        ArrayList<Integer> mediaIds = new ArrayList<>();

        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                mediaIds.add(rs.getInt("media_id"));
            }
            return mediaIds;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding rated genreIds "+e);
        }
    }

    public ArrayList<Integer> getHighlyRatedGenreIds(int userId){
        String sql = "SELECT DISTINCT mg.genre_id FROM ratings r JOIN media_genres mg ON r.media_id = mg.media_id WHERE r.user_id = ? AND r.stars >= 4";
        ArrayList<Integer> genreIds = new ArrayList<>();
        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                genreIds.add(rs.getInt("genre_id"));
            }
            return genreIds;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding highly rated genreIds "+e);
        }
    }

    public ArrayList<Media> getMediaByGenreExcluding(int genreId, ArrayList<Integer> excludeMediaIds){
        ArrayList<Media> mediaArray = new ArrayList<>();
        String sql = "SELECT DISTINCT m.id, m.title, m.description, m.media_type, m.release_year, m.age_restriction, m.creator_id " +
                     "FROM media m " +
                     "JOIN media_genres mg ON m.id = mg.media_id " +
                     "WHERE mg.genre_id = ? " +
                     "ORDER BY m.id DESC " ;

        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                int mediaId = rs.getInt("id");
                if(!excludeMediaIds.contains(mediaId)){
                    mediaArray.add(mapMedia(rs));
                }
            }
            return mediaArray;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding media entries "+e);
        }
    }
    public ArrayList<Media> getMediaByGenresExcluding(ArrayList<Integer> genreIds, ArrayList<Integer> excludeMediaIds){
        if (genreIds.isEmpty()) return new ArrayList<>();

        ArrayList<Media> mediaArray = new ArrayList<>();

        StringBuilder placeholders = new StringBuilder();
        for(int i = 0; i < genreIds.size(); i++){
            if(i > 0){
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = "SELECT DISTINCT m.id, m.title, m.description, m.media_type, m.release_year, m.age_restriction, m.creator_id " +
                     "FROM media m " +
                     "JOIN media_genres mg ON m.id = mg.media_id " +
                     "WHERE mg.genre_id IN (" + placeholders + ") " +
                     "ORDER BY m.id DESC ";

        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            int idx = 1;
            for (Integer genreId : genreIds) {
                stmt.setInt(idx++, genreId);
            }
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                int mediaId = rs.getInt("id");
                if(!excludeMediaIds.contains(mediaId)){
                    mediaArray.add(mapMedia(rs));
                }
            }
            return mediaArray;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding media entries "+e);
        }
    }

    private Media mapMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getInt("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(MediaType.fromLabel(rs.getString("media_type")));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setCreatorId(rs.getInt("creator_id"));
        return media;
    }



}
