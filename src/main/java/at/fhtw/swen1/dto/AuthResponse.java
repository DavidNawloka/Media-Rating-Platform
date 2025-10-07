package at.fhtw.swen1.dto;

import lombok.Getter;

public class AuthResponse {
    @Getter
    String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
