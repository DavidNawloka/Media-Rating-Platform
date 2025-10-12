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

    public ProfileResponse(String username, String email, Integer favoriteGenreId) {
        this.username = username;
        this.email = email;
        this.favoriteGenreId = favoriteGenreId;
    }

    public ProfileResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.favoriteGenreId = user.getFavoriteGenreId();
    }
}
