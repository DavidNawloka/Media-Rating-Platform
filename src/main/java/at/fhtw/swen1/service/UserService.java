package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.UserRepository;

import static at.fhtw.swen1.util.PasswordUtil.hashPassword;

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

        String hashedPassword = hashPassword(password);

        User user = new User(username, hashedPassword);
        return userRepository.save(user);
    }
}
