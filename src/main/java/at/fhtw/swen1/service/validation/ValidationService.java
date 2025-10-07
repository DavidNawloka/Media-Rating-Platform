package at.fhtw.swen1.service.validation;

public abstract class ValidationService {

    protected static boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }
}
