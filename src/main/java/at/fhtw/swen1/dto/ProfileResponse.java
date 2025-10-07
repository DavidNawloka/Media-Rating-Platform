package at.fhtw.swen1.dto;

import at.fhtw.swen1.model.User;
import lombok.Getter;

public class ProfileResponse {
    @Getter
    private String username;
    @Getter
    private String email;

    public ProfileResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public ProfileResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
