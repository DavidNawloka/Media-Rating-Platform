package at.fhtw.swen1.util;

import java.sql.Timestamp;
import java.util.UUID;

public class TokenUtil {
    private static final int EXPIRATION_DAYS = 1;

    public static String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static Timestamp getExpirationDate() {
        return new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRATION_DAYS);
    }
}
