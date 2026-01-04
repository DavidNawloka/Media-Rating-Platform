package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.repository.LikeRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.service.validation.ValidationService;

public class RatingService {

    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;
    private final LikeRepository likeRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository, LikeRepository likeRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
        this.likeRepository = likeRepository;
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

    public void deleteRating(int ratingId, int loggedInUserId) throws NotExistsException {

        Rating existingRating = ratingRepository.findById(ratingId);
        if(existingRating == null || existingRating.getUserId() != loggedInUserId){
            throw new NotExistsException("Rating does not exist");
        }
        ratingRepository.delete(ratingId);

    }

    public Rating updateRating(int loggedInUserId, int ratingId, int stars, String comment) throws NotExistsException {
        Rating existingRating = ratingRepository.findById(ratingId);
        if(existingRating == null || existingRating.getUserId() != loggedInUserId){
            throw new NotExistsException("Rating does not exist");
        }

        if(comment == null) comment = existingRating.getComment();

        Rating newRating = new Rating(existingRating.getMediaId(), loggedInUserId,stars,comment);
        newRating.setId(ratingId);

        return ratingRepository.update(newRating);
    }

    public void confirmRating(int loggedInUserId, int ratingId) throws NotExistsException {
        Rating existingRating = ratingRepository.findById(ratingId);
        if(existingRating == null || existingRating.getUserId() != loggedInUserId){
            throw new NotExistsException("Rating does not exist");
        }

        ratingRepository.confirmRating(ratingId);
    }

    public void likeRating(int loggedInUserId, int ratingId) throws NotExistsException, AlreadyExistsException {
        Rating existingRating = ratingRepository.findById(ratingId);
        if(existingRating == null){
            throw new NotExistsException("Rating does not exist");
        }

        if(likeRepository.find(loggedInUserId,ratingId)){
            throw new AlreadyExistsException("Rating already liked");
        }

        likeRepository.save(loggedInUserId,ratingId);
    }
}
