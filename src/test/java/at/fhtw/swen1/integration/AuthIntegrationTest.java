package at.fhtw.swen1.integration;

import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.repository.SessionRepository;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static org.junit.Assert.assertNotNull;


@Testcontainers
public class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15").withCopyFileToContainer(MountableFile.forHostPath("database/init.sql"),"/docker-entrypoint-initdb.d/init.sql");

    @BeforeAll
    static void setup(){
        System.setProperty("DB_HOST", postgres.getHost());
        System.setProperty("DB_PORT",postgres.getFirstMappedPort().toString());
        System.setProperty("DB_NAME",postgres.getDatabaseName());
        System.setProperty("DB_USER",postgres.getUsername());
        System.setProperty("DB_PASSWORD",postgres.getPassword());
    }

    @Test
    void registerAndLogin_shouldCreateUserAndSession() throws ValidationException, AlreadyExistsException, CredentialsException {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();

        UserRepository userRepository = new UserRepository();
        SessionRepository sessionRepository = new SessionRepository();
        AuthService authService = new AuthService(userRepository,sessionRepository);

        Session registerSession = authService.register(uniqueUsername,uniqueUsername + "@example.com","password123");

        assertNotNull(registerSession);
        assertNotNull(registerSession.getToken());

        Session loginSession = authService.login(uniqueUsername,"password123");

        assertNotNull(loginSession);
        assertNotNull(loginSession.getToken());

    }

}
