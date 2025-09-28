package at.fhtw.swen1.service;

import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password){
        if(username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        if(password == null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        if(userRepository.findByUsername(username) != null){
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(username, password);
        return userRepository.save(user);
    }
}
