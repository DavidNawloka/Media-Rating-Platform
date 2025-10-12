package at.fhtw.swen1.dto;

import lombok.Getter;

@Getter
public class ProfileUpdateRequest {
    String email;
    String username;
    Integer favoriteGenreId;
}
