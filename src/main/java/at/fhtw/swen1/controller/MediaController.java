package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.MediaDTO;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.MediaService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class MediaController extends Controller {
    MediaService mediaService;

    public MediaController(AuthService authService, MediaService mediaService) {
        super(authService);
        this.mediaService = mediaService;
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

            if(method.equals("POST")){
                handleCreateMedia(exchange);
            }

        }

        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleCreateMedia(HttpExchange exchange) {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);

            MediaDTO mediaInputRequest = getDTO(exchange, MediaDTO.class);

            Media newMedia = mediaService.createMedia(
                    mediaInputRequest.getTitle(),
                    mediaInputRequest.getDescription(),
                    mediaInputRequest.getMediaType(),
                    mediaInputRequest.getReleaseYear(),
                    mediaInputRequest.getAgeRestriction(),
                    mediaInputRequest.getGenreIds(),
                    loggedInUserId
            );

            MediaDTO mediaCreationResponse = new MediaDTO(newMedia);
            sendResponse(exchange,201, JsonUtil.toJson(mediaCreationResponse));




        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
