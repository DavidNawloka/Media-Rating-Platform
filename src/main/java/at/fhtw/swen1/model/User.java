package at.fhtw.swen1.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private int id;
    private String username;
    private String hashedPassword;
    private String email;
    private Integer favoriteGenreId;

    public User(){

    }
    public User(String username, String email, String hashedPassword){
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

}
