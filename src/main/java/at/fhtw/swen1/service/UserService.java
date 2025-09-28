package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) throws UserAlreadyExistsException, ValidationException {
        if(username == null || username.trim().isEmpty()){
            throw new ValidationException("Username is required");
        }
        if(password == null || password.trim().isEmpty() || password.length() < 6){
            throw new ValidationException("Password must be at least 6 characters");
        }



        if(userRepository.findByUsername(username) != null){
            throw new UserAlreadyExistsException("Username '" + username +"' already exists");
        }
        User user = new User(username, password);
        return userRepository.save(user);
    }
}
