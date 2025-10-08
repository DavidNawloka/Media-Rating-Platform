package at.fhtw.swen1.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Genre {
    private int id;
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre() {
        
    }
}
