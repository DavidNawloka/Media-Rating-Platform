package at.fhtw.swen1.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String usernameOrEmail;
    private String password;

}
