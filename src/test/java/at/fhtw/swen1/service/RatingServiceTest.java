package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.repository.LikeRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.repository.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private LikeRepository likeRepository;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository,mediaRepository,likeRepository);
    }

    @Test
    void createRating_Success() throws ValidationException, AlreadyExistsException {
        when(mediaRepository.findById(1)).thenReturn(new Media());
        when(ratingRepository.exists(1,1)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class),any(UnitOfWork.class))).thenAnswer(i -> i.getArgument(0));

        Rating result = ratingService.createRating(1,1,5, "Great!");

        assertEquals(5,result.getStars());
        assertEquals("Great!", result.getComment());
        verify(ratingRepository).save(any(Rating.class),any(UnitOfWork.class));
    }

    @Test
    void createRating_InvalidStars_ThrowsValidationException(){
        assertThrows(ValidationException.class, () -> ratingService.createRating(1,1,6, "comment"));
    }

    @Test
    void createRating_StarsZero_ThrowsValidationException(){
        assertThrows(ValidationException.class, () -> ratingService.createRating(1,1,0, "comment"));
    }

    @Test
    void createRating_MediaNotExists_ThrowsValidationException(){
        when(mediaRepository.findById(1)).thenReturn(null);

        assertThrows(ValidationException.class, () -> ratingService.createRating(1,1,5, "comment"));
    }

    @Test
    void createRating_AlreadyRated_ThrowsAlreadyExistsException(){
        when(mediaRepository.findById(1)).thenReturn(new Media());
        when(ratingRepository.exists(1,1)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> ratingService.createRating(1,1,5, "comment"));
    }

    @Test
    void deleteRating_Success() throws NotExistsException{
        Rating existing = new Rating(1,1,5, "test");
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);

        ratingService.deleteRating(1,1);
        verify(ratingRepository).delete(eq(1),any(UnitOfWork.class));
    }

    @Test
    void deleteRating_NotOwner_ThrowsNotExistsException(){
        Rating existing = new Rating(1,2,5, "test"); // userId 2 owns it
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);

        assertThrows(NotExistsException.class, () -> ratingService.deleteRating(1,1));
    }

    @Test
    void deleteRating_NotExists_ThrowsNotExistsException(){
        when(ratingRepository.findById(1)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> ratingService.deleteRating(1,1));
    }

    @Test
    void likeRating_Success() throws NotExistsException, AlreadyExistsException{
        when(ratingRepository.findById(1)).thenReturn(new Rating());
        when(likeRepository.find(1,1)).thenReturn(false);

        ratingService.likeRating(1,1);

        verify(likeRepository).save(eq(1),eq(1),any(UnitOfWork.class));
    }

    @Test
    void likeRating_AlreadyLiked_ThrowsAlreadyExistsException(){
        when(ratingRepository.findById(1)).thenReturn(new Rating());
        when(likeRepository.find(1,1)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> ratingService.likeRating(1,1));
    }

    @Test
    void likeRating_NotExists_ThrowsNotExistsException(){
        when(ratingRepository.findById(1)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> ratingService.likeRating(1,1));
    }

    @Test
    void updateRating_Success() throws NotExistsException, ValidationException {
        Rating existing = new Rating(1,1, 3, "old comment");
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);
        when(ratingRepository.update(any(Rating.class),any(UnitOfWork.class))).thenAnswer(i -> i.getArgument(0));

        Rating result = ratingService.updateRating(1,1,5,"new comment");

        assertEquals(5, result.getStars());
        assertEquals("new comment", result.getComment());
    }

    @Test
    void updateRating_NotOwner_ThrowsNotExistsException(){
        Rating existing = new Rating(1,2,3, "comment");
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);

        assertThrows(NotExistsException.class, () -> ratingService.updateRating(1,1,5,"new"));
    }
    @Test
    void updateRating_NotExists_ThrowsNotExistsException(){
        when(ratingRepository.findById(1)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> ratingService.updateRating(1,1,5,"new"));
    }

    @Test
    void confirmRating_Success() throws NotExistsException{
        Rating existing = new Rating(1,1,5, "comment");
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);


        ratingService.confirmRating(1,1);

        verify(ratingRepository).confirmRating(eq(1),any(UnitOfWork.class));
    }

    @Test
    void confirmRating_NotOwner_ThrowsNotExistsException(){
        Rating existing = new Rating(1,2,5, "comment");
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(existing);

        assertThrows(NotExistsException.class, () -> ratingService.confirmRating(1,1));
    }

    @Test
    void confirmRating_NotExists_ThrowsNotExistsException(){
        when(ratingRepository.findById(1)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> ratingService.confirmRating(1,1));
    }

    @Test
    void getUserRatings_HidesUnconfirmedComments(){
        ArrayList<Rating> ratings = new ArrayList<>();
        Rating confirmed = new Rating(1,1, 5, "visible", true);
        Rating unconfirmed = new Rating(2,1,4, "hidden", false);
        ratings.add(confirmed);
        ratings.add(unconfirmed);
        when(ratingRepository.findByUserId(1)).thenReturn(ratings);

        ArrayList<Rating> result = ratingService.getUserRatings(1);

        assertEquals("visible", result.get(0).getComment());
        assertNull(result.get(1).getComment());
    }
}