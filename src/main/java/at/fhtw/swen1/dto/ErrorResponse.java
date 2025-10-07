package at.fhtw.swen1.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String error;
    private String message;
    private int code;
    private long timestamp;

    public ErrorResponse(String error, String message, int code, long timestamp) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

}
