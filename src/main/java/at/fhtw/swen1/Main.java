package at.fhtw.swen1;

import at.fhtw.swen1.controller.Controller;
import at.fhtw.swen1.controller.UserController;
import at.fhtw.swen1.repository.SessionRepository;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.UserService;
import com.sun.net.httpserver.HttpServer;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.net.InetSocketAddress;

public class Main {
    private static HttpServer server;
    public static void main(String[] args) {
        try{
            UserRepository userRepository = new UserRepository();
            SessionRepository sessionRepository = new SessionRepository();
            UserService userService = new UserService(userRepository);
            AuthService authService = new AuthService(userRepository, sessionRepository);
            Controller controller = new UserController(userService,authService);

            server = HttpServer.create(new InetSocketAddress(8080),0);
            server.createContext("/api/users", controller);
            server.start();

            registerShutdownHandlers();

            System.out.println("Server started on http://localhost:8080");


        }catch(Exception e){
            e.printStackTrace();
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
        if(server != null){
            server.stop(2);
            System.out.println("Server stopped gracefully");
        }
    }
}