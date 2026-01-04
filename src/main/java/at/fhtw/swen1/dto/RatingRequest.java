package at.fhtw.swen1.dto;

import lombok.Getter;

@Getter
public class RatingRequest {
    private int stars;
    private String comment;
}
