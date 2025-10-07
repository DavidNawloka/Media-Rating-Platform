package at.fhtw.swen1.dto;

import lombok.Getter;

public class LoginResponse {
    @Getter
    String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
