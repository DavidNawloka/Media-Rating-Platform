package at.fhtw.swen1;

import at.fhtw.swen1.controller.*;
import at.fhtw.swen1.repository.*;
import at.fhtw.swen1.service.*;
import at.fhtw.swen1.util.ServerConfig;
import sun.misc.Signal;

public class Main {
    private static ServerConfig serverConfig;
    public static void main(String[] args) {
        try{
            // Initialize repositories
            UserRepository userRepository = new UserRepository();
            SessionRepository sessionRepository = new SessionRepository();
            GenreRepository genreRepository = new GenreRepository();
            MediaRepository mediaRepository = new MediaRepository();
            MediaGenreRepository mediaGenreRepository = new MediaGenreRepository();
            RatingRepository ratingRepository = new RatingRepository();
            LikeRepository likeRepository = new LikeRepository();
            FavoriteRepository favoriteRepository = new FavoriteRepository();

            // Initialize services
            UserService userService = new UserService(userRepository, genreRepository);
            AuthService authService = new AuthService(userRepository, sessionRepository);
            MediaService mediaService = new MediaService(mediaRepository,genreRepository,mediaGenreRepository,favoriteRepository);
            RatingService ratingService = new RatingService(ratingRepository,mediaRepository,likeRepository);
            FavoriteService favoriteService = new FavoriteService(favoriteRepository,mediaRepository);

            // Initialize controllers
            Controller authController = new AuthController(authService);
            Controller userController = new UserController(userService, authService,ratingService,mediaService);
            Controller mediaController = new MediaController(authService,mediaService,ratingService,favoriteService);
            Controller ratingController = new RatingController(authService, ratingService);

            serverConfig = new ServerConfig(8080);
            serverConfig.registerRoutes(authController, userController, mediaController,ratingController);
            serverConfig.start();

            registerShutdownHandlers();

        }catch(Exception e){
            System.out.println("Server threw exception: " +  e.getMessage());
        }
    }

    private static void registerShutdownHandlers(){
        // Handle Ctrl+C (SIGINT)
        Signal.handle(new Signal("INT"), signal -> {
            System.out.println("\nReceived interrupt signal. Shutting down server...");
            shutdown();
            System.exit(0);
        });
        // Handle SIGTERM
        Signal.handle(new Signal("TERM"), signal -> {
            System.out.println("\nReceived termination signal. Shutting down server...");
            shutdown();
            System.exit(0);
        });
    }

    private static void shutdown(){
        if(serverConfig != null){
            serverConfig.stop(2);
            System.out.println("Server stopped gracefully");
        }
    }
}