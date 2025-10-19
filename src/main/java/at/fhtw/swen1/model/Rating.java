package at.fhtw.swen1.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {
    private int id;
    private int mediaId;
    private int userId;
    private int stars;
    private String comment;
    private boolean commentConfirmed;

    public Rating(){}
    public Rating(int mediaId, int userId, int stars, String comment, boolean commentConfirmed) {
        this.mediaId = mediaId;
        this.userId = userId;
        this.stars = stars;
        this.comment = comment;
        this.commentConfirmed = commentConfirmed;
    }
}
