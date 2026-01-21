package at.fhtw.swen1.integration;

import at.fhtw.swen1.enums.MediaType;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.repository.*;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.MediaService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Testcontainers
public class IntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16").withCopyFileToContainer(MountableFile.forHostPath("database/init.sql"),"/docker-entrypoint-initdb.d/init.sql");

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

    @Test
    void createMedia() throws ValidationException, AlreadyExistsException, NotExistsException {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        String uniqueMediaName = "Movie_"+System.currentTimeMillis();

        UserRepository userRepository = new UserRepository();
        SessionRepository sessionRepository = new SessionRepository();
        AuthService authService = new AuthService(userRepository,sessionRepository);

        MediaRepository mediaRepository = new MediaRepository();
        GenreRepository genreRepository = new GenreRepository();
        MediaGenreRepository mediaGenreRepository = new MediaGenreRepository();
        FavoriteRepository favoriteRepository = new FavoriteRepository();
        RatingRepository ratingRepository = new RatingRepository();
        MediaService mediaService = new MediaService(mediaRepository,genreRepository,mediaGenreRepository,favoriteRepository, ratingRepository);


        Session session = authService.register(uniqueUsername, uniqueUsername+"test.com","password123");
        int userId = session.getUserId();

        Media media = mediaService.createMedia(uniqueMediaName, "A test movie", MediaType.MOVIE,2024,12, new int[]{1,2},userId);

        assertNotNull(media.getId());
        assertEquals(uniqueMediaName, media.getTitle());
        assertEquals(userId,media.getCreatorId());


    }

}
