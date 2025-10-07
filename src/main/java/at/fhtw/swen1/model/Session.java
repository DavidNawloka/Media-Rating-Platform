package at.fhtw.swen1.model;

import lombok.Getter;

import java.sql.Timestamp;

public class Session {
    @Getter
    private String token;
    @Getter
    private int userId;
    @Getter
    private Timestamp expiresAt;


    public Session(String token, int userId, Timestamp expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }
}
