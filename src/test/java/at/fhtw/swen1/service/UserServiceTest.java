package at.fhtw.swen1.service;

import at.fhtw.swen1.dto.ProfileResponse;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Genre;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.GenreRepository;
import at.fhtw.swen1.repository.RatingRepository;
import at.fhtw.swen1.repository.UnitOfWork;
import at.fhtw.swen1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private RatingRepository ratingRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository,genreRepository,ratingRepository);
    }

    @Test
    void getUserProfile_Success() {
        User user = new User("testuser", "test@gmail.com", "hash");
        when(userRepository.findById(1)).thenReturn(user);

        ProfileResponse result = userService.getUserProfile(1);

        assertEquals("testuser",result.getUsername());
    }

    @Test
    void getUserProfile_NotExists_ReturnsNull(){
        when(userRepository.findById(1)).thenReturn(null);

        ProfileResponse result = userService.getUserProfile(1);

        assertNull(result);
    }

    @Test
    void getLeaderboard_Success(){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1","u1@gmail.com","hash"));
        users.add(new User("user2","u2@gmail.com","hash"));
        when(userRepository.findMostActiveUsers()).thenReturn(users);

        ArrayList<ProfileResponse> result = userService.getLeaderboard();

        assertEquals(2, result.size());
    }

    @Test
    void updateUserProfile_Success() throws NotExistsException, AlreadyExistsException, ValidationException {
        try(MockedConstruction<UnitOfWork> mocked = Mockito.mockConstruction(UnitOfWork.class)) {
            when(genreRepository.getGenre(1)).thenReturn(new Genre());
            when(userRepository.findByEmail("new@gmail.com")).thenReturn(null);
            when(userRepository.findByUsername("newname")).thenReturn(null);
            User updated = new User("newname", "new@gmail.com", "hash");
            when(userRepository.update(eq("newname"), eq("new@gmail.com"), eq(1), eq(1), any(UnitOfWork.class))).thenReturn(updated);

            ProfileResponse result = userService.updateUserProfile("newname", "new@gmail.com", 1, 1);

            assertEquals("newname", result.getUsername());
        }
    }

    @Test
    void updateUserProfile_InvalidGenre_ThrowsNotExistsException(){
        when(genreRepository.getGenre(9)).thenReturn(null);

        assertThrows(NotExistsException.class, () -> userService.updateUserProfile("name","email@gmail.com",9,1));
    }

    @Test
    void updateUserProfile_DuplicateEmail_ThrowsAlreadyExistsException(){
        User existingUser = new User("other","taken@gmail.com","hash");
        existingUser.setId(2);
        when(userRepository.findByEmail("taken@gmail.com")).thenReturn(existingUser);
        when(userRepository.findByUsername("newname")).thenReturn(null);

        assertThrows(AlreadyExistsException.class, () -> userService.updateUserProfile("newname","taken@gmail.com", null, 1));
    }

}
