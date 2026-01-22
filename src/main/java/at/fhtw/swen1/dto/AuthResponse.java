package at.fhtw.swen1.dto;

import lombok.Getter;

public class AuthResponse {
    @Getter
    String token;
    @Getter
    int userId;

    public AuthResponse(String token, int userId) {
        this.token = token;
        this.userId = userId;
    }
}
