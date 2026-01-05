package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.RatingRequest;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.RatingService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class RatingController extends Controller{

    private final RatingService ratingService;

    public RatingController(AuthService authService, RatingService ratingService){
        super(authService);
        this.ratingService = ratingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if(path.matches("/api/ratings/\\d+")){
            String[] pathParts = path.split("/");
            int ratingId = parseInt(pathParts[3]);

            if(method.equals("PUT")){
                handleUpdateRating(exchange,ratingId);
            }

            if(method.equals("DELETE")){
                handleDeleteRating(exchange, ratingId);
            }
        }
        else if (path.matches("/api/ratings/\\d+/like") && method.equals("POST")){
            String[] pathParts = path.split("/");
            int ratingId = parseInt(pathParts[3]);
            handleLikeRating(exchange, ratingId);
        }
        else if (path.matches("/api/ratings/\\d+/confirm")&& method.equals("POST")){
            String[] pathParts = path.split("/");
            int ratingId = parseInt(pathParts[3]);
            handleConfirmRating(exchange, ratingId);
        }
    }

    private void handleConfirmRating(HttpExchange exchange, int ratingId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            ratingService.confirmRating(loggedInUserId, ratingId);
            sendResponse(exchange,200);


        }catch(NotExistsException e){
            handleError("Rating entry does not exist", e.getMessage(), 409, exchange);


        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleLikeRating(HttpExchange exchange, int ratingId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            ratingService.likeRating(loggedInUserId, ratingId);
            sendResponse(exchange,200);


        }catch(NotExistsException e){
            handleError("Rating entry does not exist", e.getMessage(), 409, exchange);
        }catch(AlreadyExistsException e) {
            handleError("Rating already liked", e.getMessage(), 409, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleUpdateRating(HttpExchange exchange, int ratingId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            RatingRequest ratingRequest = getDTO(exchange,RatingRequest.class);


            Rating newRating = ratingService.updateRating(
                    loggedInUserId,
                    ratingId,
                    ratingRequest.getStars(),
                    ratingRequest.getComment()
            );

            sendResponse(exchange,201, JsonUtil.toJson(newRating));

        }
        catch(NotExistsException e){
            handleError("Rating entry does not exist", e.getMessage(), 409, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleDeleteRating(HttpExchange exchange, int ratingId) throws IOException{
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            ratingService.deleteRating(ratingId, loggedInUserId);

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
