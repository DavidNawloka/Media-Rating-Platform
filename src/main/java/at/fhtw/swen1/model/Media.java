package at.fhtw.swen1.model;

import at.fhtw.swen1.enums.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Media {
    private int id;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private int ageRestriction;
    private int[] genreIds;
    private int creatorId;

    public Media(){}
    public Media(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, int[] genreIds, int creatorId) {
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genreIds = genreIds;
        this.creatorId = creatorId;
    }
}
