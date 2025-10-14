package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.ProfileResponse;
import at.fhtw.swen1.dto.ProfileUpdateRequest;
import at.fhtw.swen1.exception.GenreNotExistsException;
import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.UserService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class UserController extends Controller{
    private final UserService userService;

    public UserController(UserService userService, AuthService authService) {
        super(authService);
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if(path.matches("/api/users/\\d+/profile")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);

            if(method.equals("GET")){
                handleGetProfile(exchange,userId);
            }
            else if(method.equals("PUT")){
                handleUpdateProfile(exchange,userId);
            }

        }

        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleGetProfile(HttpExchange exchange, int requestUserId) throws IOException{
        try{
            int loggedInUserId = getLoggedInUserId(exchange);

            if(loggedInUserId != requestUserId){
                handleError("Unauthorized", "User cannot get other user", 401, exchange);
            }

            User user = userService.getUserProfile(loggedInUserId);

            ProfileResponse profileResponse = new ProfileResponse(user);

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
            if(userId != requestUserId){
                handleError("Unauthorized", "User cannot modify other user", 401, exchange);
            }

            ProfileUpdateRequest profileUpdateRequest = getDTO(exchange, ProfileUpdateRequest.class);

            User newUser = userService.updateUserProfile(profileUpdateRequest.getUsername(),profileUpdateRequest.getEmail(),profileUpdateRequest.getFavoriteGenreId(),userId);

            ProfileResponse profileResponse = new ProfileResponse(newUser);
            sendResponse(exchange,200, JsonUtil.toJson(profileResponse));

        }catch(UserAlreadyExistsException e){
            handleError("User already exists", e.getMessage(), 409, exchange);
        }
        catch(GenreNotExistsException e){
            handleError("Genre not found", e.getMessage(), 404, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }


}
