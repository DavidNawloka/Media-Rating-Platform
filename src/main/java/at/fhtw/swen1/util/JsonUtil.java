package at.fhtw.swen1.util;

import at.fhtw.swen1.exception.ValidationException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    public static String toJson(Object object) throws RuntimeException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON"+e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws ValidationException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new ValidationException("Error converting from JSON"+e);
        }
    }
}
