package at.fhtw.swen1;

import at.fhtw.swen1.controller.Controller;
import at.fhtw.swen1.controller.AuthController;
import at.fhtw.swen1.controller.MediaController;
import at.fhtw.swen1.controller.UserController;
import at.fhtw.swen1.repository.*;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.MediaService;
import at.fhtw.swen1.service.UserService;
import at.fhtw.swen1.util.ServerConfig;
import com.sun.net.httpserver.HttpServer;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.net.InetSocketAddress;

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

            // Initialize services
            UserService userService = new UserService(userRepository, genreRepository);
            AuthService authService = new AuthService(userRepository, sessionRepository);
            MediaService mediaService = new MediaService(mediaRepository,genreRepository,mediaGenreRepository);

            // Initialize controllers
            Controller authController = new AuthController(authService);
            Controller userController = new UserController(userService, authService);
            Controller mediaController = new MediaController(authService,mediaService);

            serverConfig = new ServerConfig(8080);
            serverConfig.registerRoutes(authController, userController, mediaController);
            serverConfig.start();

            registerShutdownHandlers();

        }catch(Exception e){
            System.out.println("Server threw exception: " +  e.getMessage());
        }
    }

    private static void registerShutdownHandlers(){
        // Handle Ctrl+C (SIGINT)
        Signal.handle(new Signal("INT"), new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                System.out.println("\nReceived interrupt signal. Shutting down server...");
                shutdown();
                System.exit(0);
            }
        });
        // Handle SIGTERM
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            @Override
            public void handle(Signal signal){
                System.out.println("\nReceived termination signal. Shutting down server...");
                shutdown();
                System.exit(0);
            }
        });
    }

    private static void shutdown(){
        if(serverConfig != null){
            serverConfig.stop(2);
            System.out.println("Server stopped gracefully");
        }
    }
}