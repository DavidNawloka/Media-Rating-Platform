package at.fhtw.swen1.dto;

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

    public String getError() {
        return error;
    }
    public String getMessage() {
        return message;
    }
    public int getCode() {
        return code;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
