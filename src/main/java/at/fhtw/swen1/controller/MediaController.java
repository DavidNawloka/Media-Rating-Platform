package at.fhtw.swen1.controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class MediaController extends Controller {

    public MediaController() {

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if(path.matches("/api/media/\\d+")){
            String[] pathParts = path.split("/");
            int mediaId = parseInt(pathParts[3]);

            // Handle Delete, Update and Get
        }
        if(path.equals("/api/media")){

            if(method.equals("PUT")){
                handleCreateMedia(exchange);
            }

        }

        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleCreateMedia(HttpExchange exchange) {

    }
}
