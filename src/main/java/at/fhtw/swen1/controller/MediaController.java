package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.MediaRequest;
import at.fhtw.swen1.dto.RatingRequest;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.FavoriteService;
import at.fhtw.swen1.service.MediaService;
import at.fhtw.swen1.service.RatingService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class MediaController extends Controller {
    final MediaService mediaService;
    final RatingService ratingService;
    final FavoriteService favoriteService;

    public MediaController(AuthService authService, MediaService mediaService, RatingService ratingService, FavoriteService favoriteService) {
        super(authService);
        this.mediaService = mediaService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if(path.matches("/api/media/\\d+")){
            String[] pathParts = path.split("/");
            int mediaId = parseInt(pathParts[3]);

            if(method.equals("PUT")){
                handleUpdateMedia(exchange,mediaId);
            }

            if(method.equals("GET")){
                handleGetMedia(exchange,mediaId);
            }

            if(method.equals("DELETE")){
                handleDeleteMedia(exchange, mediaId);
            }
        }
        else if (path.matches("/api/media/\\d+/rate") && method.equals("POST")){
            String[] pathParts = path.split("/");
            int mediaId = parseInt(pathParts[3]);
            handleRateMedia(exchange, mediaId);
        }
        else if(path.equals("/api/media")){

            if(method.equals("POST")){
                handleCreateMedia(exchange);
            }

        }
        else if(path.matches("/api/media/\\d+/favorite")){
            String[] pathParts = path.split("/");
            int mediaId = parseInt(pathParts[3]);

            if(method.equals("POST")){
                handleFavoriteMedia(exchange, mediaId);
            }

            if(method.equals("DELETE")){
                handleUnfavoriteMedia(exchange, mediaId);
            }
        }

        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleFavoriteMedia(HttpExchange exchange, int mediaId) throws IOException {
        try{

            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            favoriteService.saveFavorite(loggedInUserId,mediaId);
            sendResponse(exchange,200);


        }catch(AlreadyExistsException e){
            handleError("Media already favorite", e.getMessage(), 409, exchange);
        }catch(NotExistsException e){
            handleError("Media does not exist", e.getMessage(), 404, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }


    private void handleUnfavoriteMedia(HttpExchange exchange, int mediaId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            favoriteService.deleteFavorite(loggedInUserId,mediaId);
            sendResponse(exchange,204);


        }catch(NotExistsException e){
            handleError("Media has not been favorite yet", e.getMessage(), 409, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }


    private void handleCreateMedia(HttpExchange exchange) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            MediaRequest mediaInputRequest = getDTO(exchange, MediaRequest.class);

            Media newMedia = mediaService.createMedia(
                    mediaInputRequest.getTitle(),
                    mediaInputRequest.getDescription(),
                    mediaInputRequest.getMediaType(),
                    mediaInputRequest.getReleaseYear(),
                    mediaInputRequest.getAgeRestriction(),
                    mediaInputRequest.getGenreIds(),
                    loggedInUserId
            );

            sendResponse(exchange,201, JsonUtil.toJson(newMedia));




        }catch(ValidationException e){
            handleError("Media entry data incorrect", e.getMessage(), 409, exchange);

        }catch(NotExistsException e){
            handleError("Genre not found", e.getMessage(), 404, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetMedia(HttpExchange exchange, int mediaId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            Media media = mediaService.getMedia(mediaId, loggedInUserId, false);


            sendResponse(exchange,200, JsonUtil.toJson(media));

        }catch(NotExistsException e){
            handleError("Media entry does not exist", e.getMessage(), 409, exchange);

        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleRateMedia(HttpExchange exchange, int mediaId) throws IOException{
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            RatingRequest ratingRequest = getDTO(exchange, RatingRequest.class);

            Rating rating = ratingService.createRating(mediaId,loggedInUserId,ratingRequest.getStars(),ratingRequest.getComment());
            sendResponse(exchange,201, JsonUtil.toJson(rating));

        }catch(ValidationException e){
            handleError("Rating entry data incorrect", e.getMessage(), 409, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleUpdateMedia(HttpExchange exchange, int mediaId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            MediaRequest mediaInputRequest = getDTO(exchange, MediaRequest.class);


            Media newMedia = mediaService.updateMedia(
                    loggedInUserId,
                    mediaId,
                    mediaInputRequest.getTitle(),
                    mediaInputRequest.getDescription(),
                    mediaInputRequest.getMediaType(),
                    mediaInputRequest.getReleaseYear(),
                    mediaInputRequest.getAgeRestriction(),
                    mediaInputRequest.getGenreIds(),
                    loggedInUserId
            );

            sendResponse(exchange,201, JsonUtil.toJson(newMedia));

        }
        catch(NotExistsException e){
            handleError("Media entry or genre does not exist", e.getMessage(), 409, exchange);

        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleDeleteMedia(HttpExchange exchange, int mediaId) throws IOException{
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            mediaService.deleteMedia(mediaId, loggedInUserId);

            sendResponse(exchange,204);

        }catch(NotExistsException e){
            handleError("Media entry does not exist", e.getMessage(), 409, exchange);

        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }
}
