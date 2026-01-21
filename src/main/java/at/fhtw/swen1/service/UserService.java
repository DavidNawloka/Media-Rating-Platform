package at.fhtw.swen1.service;

import at.fhtw.swen1.dto.ProfileResponse;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.GenreRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.repository.UnitOfWork;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.service.validation.AuthValidationService;

import java.util.ArrayList;

public class UserService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;
    public UserService(UserRepository userRepository, GenreRepository genreRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    public ProfileResponse getUserProfile(int userId){
        User user =  userRepository.findById(userId);
        if(user == null){
            return null;
        }
        ArrayList<Rating> ratings = ratingRepository.findByUserId(userId);

        int totalRatings = ratings.size();
        float averageScore = (float) ratings.stream().mapToInt(Rating::getStars).average().orElse(0);
        return new ProfileResponse(user, totalRatings, averageScore);

    }

    public ArrayList<ProfileResponse> getLeaderboard(){

        ArrayList<User> users = userRepository.findMostActiveUsers();

        ArrayList<ProfileResponse> profiles = new ArrayList<>();
        for(User user : users){
            ArrayList<Rating> ratings = ratingRepository.findByUserId(user.getId());

            int totalRatings = ratings.size();
            float averageScore = (float) ratings.stream().mapToInt(Rating::getStars).average().orElse(0);
            profiles.add(new ProfileResponse(user,totalRatings,averageScore));
        }

        return profiles;
    }

    public ProfileResponse updateUserProfile(String username, String email, Integer favoriteGenreId, int userId) throws NotExistsException, AlreadyExistsException, ValidationException {

        if(username == null || username.isEmpty() || email == null || email.isEmpty()){
            throw new ValidationException("No username or email was given! ");
        }

        if(favoriteGenreId != null && genreRepository.getGenre(favoriteGenreId) == null){
            throw new NotExistsException("Genre ID: " + favoriteGenreId + " does not exist.");
        }


        User existingUserEmail = userRepository.findByEmail(email);
        User existingUserUsername = userRepository.findByUsername(username);

        if((existingUserEmail != null && existingUserEmail.getId() != userId) || (existingUserUsername != null && existingUserUsername.getId() != userId)){
            throw new AlreadyExistsException("Username or email already exists");

        }
        ArrayList<Rating> ratings = ratingRepository.findByUserId(userId);

        int totalRatings = ratings.size();
        float averageScore = (float) ratings.stream().mapToInt(Rating::getStars).average().orElse(0);

        try(UnitOfWork uow = new UnitOfWork()){
            User userUpdated = userRepository.update(username, email, favoriteGenreId, userId, uow);
            uow.commitTransaction();
            return new ProfileResponse(userUpdated,totalRatings,averageScore);
        }

    }


}
