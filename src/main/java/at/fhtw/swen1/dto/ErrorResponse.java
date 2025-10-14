package at.fhtw.swen1.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;
    private final String message;
    private final int code;
    private final long timestamp;

    public ErrorResponse(String error, String message, int code, long timestamp) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

}
