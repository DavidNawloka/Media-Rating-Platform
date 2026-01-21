package at.fhtw.swen1.service;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Genre;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private MediaGenreRepository mediaGenreRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private RatingRepository ratingRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(mediaRepository,genreRepository,mediaGenreRepository,favoriteRepository,ratingRepository);
    }

    @Test
    void createMedia_Success() throws NotExistsException, ValidationException{
        try(MockedConstruction<UnitOfWork> mocked = Mockito.mockConstruction(UnitOfWork.class)) {
            when(genreRepository.getGenre(1)).thenReturn(new Genre());
            Media savedMedia = new Media("Title", "Description", MediaType.MOVIE, 2024, 12, new int[]{1}, 1);
            savedMedia.setId(1);
            when(mediaRepository.save(any(Media.class), any(UnitOfWork.class))).thenReturn(savedMedia);

            Media result = mediaService.createMedia("Title", "Description", MediaType.MOVIE, 2024, 12, new int[]{1}, 1);

            assertEquals("Title", result.getTitle());
            verify(mediaGenreRepository).save(eq(1), eq(1), any(UnitOfWork.class));
        }
    }

    @Test
    void createMedia_EmptyTitle_ThrowsValidationException(){
        assertThrows(ValidationException.class, () -> mediaService.createMedia("", "description", MediaType.GAME, 2024,12, new int[]{1},1));
    }

    @Test
    void createMedia_InvalidGenre_ThrowsNotExistsException(){
        when(genreRepository.getGenre(9)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> mediaService.createMedia("title", "description", MediaType.GAME, 2024,12, new int[]{9},1));
    }
    
    @Test
    void getMedia_Success() throws NotExistsException{
        Media savedMedia = new Media("Title", "Description", MediaType.MOVIE, 2024,12, new int[]{1},1);
        savedMedia.setId(1);
        
        when(mediaRepository.findById(1)).thenReturn(savedMedia);
        when(mediaGenreRepository.findGenreIdsByMediaId(1)).thenReturn(new int[]{1});
        
        Media result = mediaService.getMedia(1,1,false);
        
        assertEquals("Title", result.getTitle());
    }

    @Test
    void getMedia_NotExists_ThrowsNotExistsException(){
        when(mediaRepository.findById(1)).thenReturn(null);
        assertThrows(NotExistsException.class, () -> mediaService.getMedia(1,1, false));
    }

    @Test
    void deleteMedia_Success() throws NotExistsException{
        try(MockedConstruction<UnitOfWork> mocked = Mockito.mockConstruction(UnitOfWork.class)) {
            Media media = new Media();
            media.setCreatorId(1);
            when(mediaRepository.findById(1)).thenReturn(media);

            mediaService.deleteMedia(1, 1);

            verify(mediaRepository).delete(eq(1), any(UnitOfWork.class));
        }
    }

    @Test
    void deleteMedia_NotOwner_ThrowsNotExistsException(){
        Media media = new Media();
        media.setCreatorId(2);
        when(mediaRepository.findById(1)).thenReturn(media);

        assertThrows(NotExistsException.class, () -> mediaService.deleteMedia(1,1));
    }
}