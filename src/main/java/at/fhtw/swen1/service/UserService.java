package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.AlreadyExistsException;
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

    public User updateUserProfile(String username, String email, Integer favoriteGenreId, int userId) throws NotExistsException, AlreadyExistsException {

        if(favoriteGenreId != null && genreRepository.getGenre(favoriteGenreId) == null){
            throw new NotExistsException("Genre ID: " + favoriteGenreId + " does not exist.");
        }


        User existingUserEmail = userRepository.findByEmail(email);
        User existingUserUsername = userRepository.findByUsername(username);

        if((existingUserEmail != null && existingUserEmail.getId() != userId) || (existingUserUsername != null && existingUserUsername.getId() != userId)){
            throw new AlreadyExistsException("Username or email already exists");

        }

        return userRepository.update(username, email, favoriteGenreId, userId);
    }


}
