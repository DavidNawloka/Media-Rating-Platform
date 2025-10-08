package at.fhtw.swen1.service;

import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserProfile(int userId){
        return userRepository.findById(userId);
    }

    public User updateUserProfile(String username, String email, int userId){
        return userRepository.update(username, email, userId);
    }


}
