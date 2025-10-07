package at.fhtw.swen1.model;

public class User {
    private int id;
    private String username;
    private String hashedPassword;
    private String email;

    public User(){

    }
    public User(String username, String hashedPassword){
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
