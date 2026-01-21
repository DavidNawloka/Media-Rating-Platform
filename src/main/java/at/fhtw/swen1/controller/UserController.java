package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.ProfileResponse;
import at.fhtw.swen1.dto.ProfileUpdateRequest;
import at.fhtw.swen1.exception.NotExistsException;
import at.fhtw.swen1.exception.AlreadyExistsException;
import at.fhtw.swen1.model.Media;
import at.fhtw.swen1.model.Rating;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.service.*;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class UserController extends Controller{
    private final UserService userService;
    private final RatingService ratingService;
    private final MediaService mediaService;
    private final RecommendationService recommendationService;

    public UserController(UserService userService, AuthService authService, RatingService ratingService, MediaService mediaService, RecommendationService recommendationService) {
        super(authService);
        this.userService = userService;
        this.ratingService = ratingService;
        this.mediaService = mediaService;
        this.recommendationService = recommendationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if(path.equals("/api/leaderboard") && method.equals("GET")){
            handleGetLeaderboard(exchange);
        }
        else if(path.matches("/api/users/\\d+/profile")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);

            if(method.equals("GET")){
                handleGetProfile(exchange,userId);
            }
            else if(method.equals("PUT")){
                handleUpdateProfile(exchange,userId);
            }
            else{
                handleError("Method not allowed", "Incorrect method", 405, exchange);
            }

        }
        else if(path.matches("/api/users/\\d+/recommendations") && method.equals("GET")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);
            handleGetRecommendations(exchange, userId);
        }
        else if(path.matches("/api/users/\\d+/ratings") && method.equals("GET")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);

            handleGetRatings(exchange, userId);
        }
        else if(path.matches("/api/users/\\d+/favorites") && method.equals("GET")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);

            handleGetFavorites(exchange, userId);
        }
        else{
            handleError("Not found", "Incorrect path", 404, exchange);
        }
    }

    private void handleGetLeaderboard(HttpExchange exchange) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            ArrayList<User> leaderboard = userService.getLeaderboard();

            String responseJson = JsonUtil.toJson(leaderboard);
            sendResponse(exchange, 200, responseJson);
        }catch (Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetRecommendations(HttpExchange exchange, int userId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            if(loggedInUserId != userId){
                handleError("Unauthorized", "User cannot get other user", 401, exchange);
                return;
            }


            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);
            String type = params.get("type");
            if(type == null){
                type = "genre";
            }

            ArrayList<Media> recommendedMedias = recommendationService.getRecommendations(userId,type.equals("genre"));

            String responseJson = JsonUtil.toJson(recommendedMedias);
            sendResponse(exchange,200, responseJson);

        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetFavorites(HttpExchange exchange, int userId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            if(loggedInUserId != userId){
                handleError("Unauthorized", "User cannot get other user", 401, exchange);
                return;
            }


            ArrayList<Media> favoriteMedias = mediaService.getFavoriteMedias(loggedInUserId);

            String responseJson = JsonUtil.toJson(favoriteMedias);
            sendResponse(exchange,200, responseJson);

        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetRatings(HttpExchange exchange, int userId) throws IOException {
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            if(loggedInUserId != userId){
                handleError("Unauthorized", "User cannot get other user", 401, exchange);
                return;
            }


            ArrayList<Rating> ratings = ratingService.getUserRatings(loggedInUserId);

            String responseJson = JsonUtil.toJson(ratings);
            sendResponse(exchange,200, responseJson);

        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetProfile(HttpExchange exchange, int requestUserId) throws IOException{
        try{
            int loggedInUserId = getLoggedInUserId(exchange);
            if(loggedInUserId == -1) return;

            if(loggedInUserId != requestUserId){
                handleError("Unauthorized", "User cannot get other user", 401, exchange);
                return;
            }

            ProfileResponse profileResponse = userService.getUserProfile(loggedInUserId);


            String responseJson = JsonUtil.toJson(profileResponse);
            sendResponse(exchange,200, responseJson);

        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }



    private void handleUpdateProfile(HttpExchange exchange, int requestUserId) throws IOException{
        try{
            int userId = getLoggedInUserId(exchange);
            if(userId == -1) return;

            if(userId != requestUserId){
                handleError("Unauthorized", "User cannot modify other user", 401, exchange);
                return;
            }

            ProfileUpdateRequest profileUpdateRequest = getDTO(exchange, ProfileUpdateRequest.class);

            ProfileResponse profileResponse = userService.updateUserProfile(profileUpdateRequest.getUsername(),profileUpdateRequest.getEmail(),profileUpdateRequest.getFavoriteGenreId(),userId);

            sendResponse(exchange,200, JsonUtil.toJson(profileResponse));

        }catch(AlreadyExistsException e){
            handleError("User already exists", e.getMessage(), 409, exchange);
        }
        catch(NotExistsException e){
            handleError("Genre not found", e.getMessage(), 404, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }


}
