package at.fhtw.swen1.repository;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaGenreRepository {

    public int[] findGenreIdsByMediaId(int mediaId){
        String sql = "SELECT genre_id FROM media_genres WHERE media_id = ?";
        try(Connection conn = DatabaseManager.INSTANCE.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, mediaId);

            ResultSet rs = stmt.executeQuery();

            java.util.List<Integer> genreList = new java.util.ArrayList<>();
            while(rs.next()){
                genreList.add(rs.getInt("genre_id"));
            }
            return genreList.stream().mapToInt(Integer::intValue).toArray();
        }catch(SQLException e){
            throw new RuntimeException("Database error while find media_genre entries" + e.getMessage());
        }
    }

    public void save(int mediaId, int genreId, UnitOfWork uow) {
        String sql = "INSERT INTO media_genres (media_id, genre_id) VALUES (?, ?)";
        try{
            PreparedStatement stmt = uow.prepareStatement(sql);
            stmt.setInt(1, mediaId);
            stmt.setInt(2, genreId);

            stmt.executeUpdate();


        }catch (SQLException e){
            throw new RuntimeException("Database error while creating media_genre entry"+e.getMessage());
        }
    }

    public void delete(int mediaId, int genreId, UnitOfWork uow) {
        String sql = "DELETE FROM media_genres WHERE media_id = ? AND genre_id = ?";
        try{
            PreparedStatement stmt = uow.prepareStatement(sql);

            stmt.setInt(1, mediaId);
            stmt.setInt(2, genreId);

            stmt.executeUpdate();


        }catch (SQLException e){
            throw new RuntimeException("Database error while deleting media_genre entry"+e.getMessage());
        }
    }
}
