package at.fhtw.swen1.enums;

public enum MediaType {
    MOVIE("movie"),
    SERIES("series"),
    BOOK("book");

    public final String label;

    MediaType(String label) {
        this.label = label;
    }
}
