package at.fhtw.swen1.util;

import com.password4j.Hash;
import com.password4j.HashChecker;
import com.password4j.Password;

public class HashUtil {
    public static String hashString(String string) {
        Hash hash = Password.hash(string).withBcrypt();
        return hash.getResult();
    }
    public static boolean isEqualStringHash(String string, String hash) {
        HashChecker hashChecker = Password.check(string, hash);
        return hashChecker.withBcrypt();
    }

}
