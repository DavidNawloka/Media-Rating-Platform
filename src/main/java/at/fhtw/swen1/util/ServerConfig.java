package at.fhtw.swen1.util;

import at.fhtw.swen1.controller.Controller;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerConfig {
    private final HttpServer server;
    @Getter
    private final int port;

    public ServerConfig(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.port = port;
    }

    public void registerRoutes(Controller authController,
                               Controller userController,
                               Controller mediaController,
                               Controller ratingController) {

        server.createContext("/api/users/register", authController);
        server.createContext("/api/users/login", authController);

        server.createContext("/api/users", userController);
        server.createContext("/api/leaderboard", userController);

        server.createContext("/api/media", mediaController);

        server.createContext("/api/ratings",ratingController);

    }

    public void start(){
        server.start();
        System.out.println("Server started on http://localhost:" + port);
    }

    public void stop(int delay){
        server.stop(delay);
    }

}
