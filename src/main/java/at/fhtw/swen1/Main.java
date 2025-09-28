package at.fhtw.swen1;

import at.fhtw.swen1.controller.UserController;
import at.fhtw.swen1.repository.UserRepository;
import at.fhtw.swen1.service.UserService;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        try{
            UserRepository userRepository = new UserRepository();
            UserService userService = new UserService(userRepository);
            UserController userController = new UserController(userService);

            HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
            server.createContext("/api/users", userController);
            server.start();

            System.out.println("Server started on http://localhost:8080");


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}