package at.fhtw.swen1.service;

import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.repository.SessionRepository;
import at.fhtw.swen1.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        UserRepository userRepository = new UserRepository();
        SessionRepository sessionRepository = new SessionRepository();
        authService = new AuthService(userRepository,sessionRepository);
    }

    @Test
    void register() throws ValidationException, UserAlreadyExistsException {
        Session session  = authService.register("username","email","password");

        assertNotNull(session);
    }

    @Test
    void login() {
    }

    @Test
    void getLoggedInUser() {
    }
}