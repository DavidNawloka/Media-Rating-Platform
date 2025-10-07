package at.fhtw.swen1.exception;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
