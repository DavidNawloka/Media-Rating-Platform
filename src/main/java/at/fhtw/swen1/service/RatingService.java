package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.service.validation.ValidationService;

public class RatingService {

    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }


    public Rating createRating(int mediaId, int userId, int stars, String comment) throws ValidationException {
        if(mediaId < 0 || userId < 0 || stars < 1 || stars > 5){
            throw new ValidationException("Invalid rating data");
        }

        if(mediaRepository.findById(mediaId) == null){
            throw new ValidationException("Invalid rating data");
        }

        Rating rating = new Rating(mediaId, userId, stars, comment);
        Rating createdRating = ratingRepository.save(rating);

        return createdRating;
    }
}
