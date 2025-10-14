package at.fhtw.swen1.service.validation;

public class ValidationService {

    public static boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }
}
