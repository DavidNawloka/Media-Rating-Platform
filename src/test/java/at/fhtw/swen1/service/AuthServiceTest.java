package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.repository.SessionRepository;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository,sessionRepository);
    }

    @Test
    void register_Success() throws AlreadyExistsException, ValidationException{
        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(userRepository.findByEmail("new@email.com")).thenReturn(null);
        User savedUser = new User("newuser","new@email.com", "hash");
        savedUser.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Session result = authService.register("newuser","new@email.com","password123");
        assertNotNull(result);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void register_EmptyUsername_ThrowsValidationException(){
        assertThrows(ValidationException.class, () -> authService.register("","email@test.com","password123"));
    }

    @Test
    void register_ShortPassword_ThrowsValidationException(){
        assertThrows(ValidationException.class, () -> authService.register("","email@test.com","short"));
    }

    @Test
    void register_DuplicateUsername_ThrowsAlreadyExistsException(){
        when(userRepository.findByUsername("taken")).thenReturn(new User());

        assertThrows(AlreadyExistsException.class, () -> authService.register("taken","email@test.com","password123"));
    }
    @Test
    void register_DuplicateEmail_ThrowsAlreadyExistsException(){
        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(userRepository.findByEmail("taken@email.com")).thenReturn(new User());

        assertThrows(AlreadyExistsException.class, () -> authService.register("newuser","taken@email.com","password123"));
    }

    @Test
    void login_Success() throws ValidationException, CredentialsException {
        User user = new User("testuser","test@email.com", HashUtil.hashString("password123"));
        user.setId(1);
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        Session result = authService.login("testuser","password123");

        assertNotNull(result);
    }

    @Test
    void login_WrongPassword_ThrowsCredentialsException(){
        User user = new User("testuser","test@gmail.com", HashUtil.hashString("password123"));
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        assertThrows(CredentialsException.class, () -> authService.login("testuser","wrongpassword"));
    }

    @Test
    void login_UserNotFound_ThrowsCredentialsException(){
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        when(userRepository.findByEmail("unknown")).thenReturn(null);

        assertThrows(CredentialsException.class, () -> authService.login("unknown","password123"));
    }

}