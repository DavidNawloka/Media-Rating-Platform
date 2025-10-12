package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.GenreNotExistsException;
import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.GenreRepository;
import at.fhtw.swen1.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    public UserService(UserRepository userRepository, GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
    }

    public User getUserProfile(int userId){
        return userRepository.findById(userId);
    }

    public User updateUserProfile(String username, String email, Integer favoriteGenreId, int userId) throws GenreNotExistsException, UserAlreadyExistsException {

        if(favoriteGenreId != null && genreRepository.getGenre(favoriteGenreId) == null){
            throw new GenreNotExistsException("Genre ID: " + favoriteGenreId + " does not exist.");
        }

        if(userRepository.findByEmail(email) != null || userRepository.findByUsername(username) != null){
            throw new UserAlreadyExistsException("Username or email already exists");
        }


        return userRepository.update(username, email, favoriteGenreId, userId);
    }


}
