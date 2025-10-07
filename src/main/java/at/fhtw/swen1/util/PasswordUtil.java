package at.fhtw.swen1.util;

import com.password4j.Hash;
import com.password4j.HashChecker;
import com.password4j.Password;

public class PasswordUtil {
    public static String hashPassword(String password) {
        Hash hash = Password.hash(password).withBcrypt();
        return hash.getResult();
    }
    public static boolean checkPassword(String password, String hashedPassword) {
        HashChecker hashChecker = Password.check(password, hashedPassword);
        return hashChecker.withBcrypt();
    }

}
