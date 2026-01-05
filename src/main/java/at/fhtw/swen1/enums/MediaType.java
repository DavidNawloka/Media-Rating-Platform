package at.fhtw.swen1.enums;

public enum MediaType {
    MOVIE("movie"),
    SERIES("series"),
    GAME("game");

    public final String label;

    MediaType(String label) {
        this.label = label;
    }

    public static MediaType fromLabel(String label){
        for(MediaType mediaType : MediaType.values()){
            if(mediaType.label.equals(label)){
                return mediaType;
            }
        }
        return null;
    }
}
