package at.fhtw.swen1.dto;

import at.fhtw.swen1.model.User;
import lombok.Getter;

public class ProfileResponse {
    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private Integer favoriteGenreId;
    @Getter
    private int totalRatings;
    @Getter
    private float averageScore;

    public ProfileResponse(String username, String email, Integer favoriteGenreId, int totalRatings, float averageScore) {
        this.username = username;
        this.email = email;
        this.favoriteGenreId = favoriteGenreId;
        this.totalRatings = totalRatings;
        this.averageScore = averageScore;
    }

    public ProfileResponse(User user, int totalRatings, float averageScore) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.favoriteGenreId = user.getFavoriteGenreId();
        this.totalRatings = totalRatings;
        this.averageScore = averageScore;
    }
}
