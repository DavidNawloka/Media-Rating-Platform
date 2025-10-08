package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.GenreNotExistsException;
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

    public User updateUserProfile(String username, String email, Integer favoriteGenreId, int userId) throws GenreNotExistsException {

        if(favoriteGenreId != null && genreRepository.getGenre(favoriteGenreId) == null){
            throw new GenreNotExistsException("Genre ID: " + favoriteGenreId + " does not exist.");
        }


        return userRepository.update(username, email, favoriteGenreId, userId);
    }


}
