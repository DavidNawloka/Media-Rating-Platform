package at.fhtw.swen1.service.validation;

public class AuthValidationService extends ValidationService{

    public static boolean isValidUsername(String username){
        return !isNullOrEmpty(username);
    }

    public static boolean isValidUsernameOrEmail(String usernameOrEmail){
        return isValidUsername(usernameOrEmail) || isValidEmail(usernameOrEmail);
    }

    public static boolean isValidEmail(String email){
        return !isNullOrEmpty(email);
    }

    public static boolean isValidPassword(String password){
        return !isNullOrEmpty(password);
    }

    public static boolean isSecurePassword(String password){
        return password.length() >= 6;
    }
}
