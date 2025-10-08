package at.fhtw.swen1.dto;

import lombok.Getter;

public class ProfileUpdateRequest {
    @Getter
    String email;
    @Getter
    String username;
}
