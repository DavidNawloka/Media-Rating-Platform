package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.repository.FavoriteRepository;
import at.fhtw.swen1.repository.MediaRepository;
import at.fhtw.swen1.repository.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private MediaRepository mediaRepository;

    private FavoriteService favoriteService;

    @BeforeEach
    void setUp(){
        favoriteService = new FavoriteService(favoriteRepository,mediaRepository);
    }

    @Test
    void saveFavorite_Success() throws AlreadyExistsException, NotExistsException{
        when(favoriteRepository.exists(1,1)).thenReturn(false);
        when(mediaRepository.findById(1)).thenReturn(new Media());

        favoriteService.saveFavorite(1,1);

        verify(favoriteRepository).save(eq(1), eq(1), any(UnitOfWork.class));
    }

    @Test
    void saveFavorite_AlreadyFavorited_ThrowsAlreadyExistsException(){
        when(favoriteRepository.exists(1,1)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> favoriteService.saveFavorite(1,1));
    }

    @Test
    void saveFavorite_MediaNotExists_ThrowsNotExistsException(){
        when(favoriteRepository.exists(1,1)).thenReturn(false);
        when(mediaRepository.findById(1)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> favoriteService.saveFavorite(1,1));
    }

    @Test
    void deleteFavorite_Success() throws NotExistsException {
        when(favoriteRepository.exists(1,1)).thenReturn(true);

        favoriteService.deleteFavorite(1,1);

        verify(favoriteRepository).delete(eq(1), eq(1), any(UnitOfWork.class));
    }

    @Test
    void deleteFavorite_NotFavorited_ThrowsNotExistsException(){
        when(favoriteRepository.exists(1,1)).thenReturn(false);

        assertThrows(NotExistsException.class, () -> favoriteService.deleteFavorite(1,1));
    }

}