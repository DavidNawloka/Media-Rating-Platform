package at.fhtw.swen1.dto;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.model.Media;
import lombok.Getter;

@Getter
public class MediaRequest {
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private int ageRestriction;
    private int[] genreIds;

    public MediaRequest(){}


    public MediaRequest(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds) {
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genreIds = genreIds;
    }

    public MediaRequest(Media mediaDTO){
        this.title = mediaDTO.getTitle();
        this.description = mediaDTO.getDescription();
        this.mediaType = mediaDTO.getMediaType();
        this.releaseYear = mediaDTO.getReleaseYear();
        this.ageRestriction = mediaDTO.getAgeRestriction();
        this.genreIds = mediaDTO.getGenreIds();
    }
}
