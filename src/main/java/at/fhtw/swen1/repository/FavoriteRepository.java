package at.fhtw.swen1.repository;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FavoriteRepository {

    public ArrayList<Media> findByUserId(int userId){
        String sql = "SELECT * FROM favorites INNER JOIN public.media m on favorites.media_id = m.id WHERE user_id = ?";
        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Media> favoriteMedias = new ArrayList<>();

            while(rs.next()){
                Media media = new Media();
                media.setId(rs.getInt("id"));
                media.setTitle(rs.getString("title"));
                media.setDescription(rs.getString("description"));
                media.setAgeRestriction(rs.getInt("age_restriction"));
                media.setReleaseYear(rs.getInt("release_year"));
                media.setMediaType(MediaType.fromLabel(rs.getString("media_type")));
                media.setCreatorId(rs.getInt("creator_id"));
                favoriteMedias.add(media);
            }
            return favoriteMedias;

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding like entry "+e);
        }
    }


    public boolean exists(int userId, int mediaId){
        String sql = "SELECT * FROM favorites WHERE user_id = ? AND media_id = ?";

        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        }catch (SQLException e){
            throw new RuntimeException("Database error while finding like entry "+e);
        }
    }

    public void save(int userId, int mediaId, UnitOfWork uow) {
        String sql = "INSERT INTO favorites (user_id, media_id) VALUES (?, ?)";
        try{
            PreparedStatement stmt = uow.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating favorite entry",e);
        }
    }

    public void delete(int userId, int mediaId, UnitOfWork uow) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";
        try{
            PreparedStatement stmt = uow.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Database error while creating favorite entry",e);
        }
    }
}
