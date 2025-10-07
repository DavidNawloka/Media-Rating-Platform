package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.AuthRequest;
import at.fhtw.swen1.dto.AuthResponse;
import at.fhtw.swen1.dto.ProfileResponse;
import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.service.UserService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class UserController extends Controller {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if(path.equals("/api/users/register") && method.equals("POST")){
            handleRegister(exchange);
        }
        if(path.equals("/api/users/login") && method.equals("POST")){
            handleLogin(exchange);
        }
        if(path.matches("/api/users/\\d+/profile") && method.equals("GET")){
            String[] pathParts = path.split("/");
            int userId = parseInt(pathParts[3]);
            handleGetProfile(exchange,userId);
        }
        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleLogin(HttpExchange exchange) throws IOException{
        try{
            AuthRequest authRequest = getDTO(exchange, AuthRequest.class);

            Session session = authService.login(authRequest.getUsername(), authRequest.getPassword());

            AuthResponse response = new AuthResponse(session.getToken());

            String responseJson = JsonUtil.toJson(response);
            sendResponse(exchange,200, responseJson);


        }catch(ValidationException e){
            handleError("Validation error", e.getMessage(), 400, exchange);
        }catch(CredentialsException e){
            handleError("Credentials error", e.getMessage(), 400, exchange);
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }


    private void handleRegister(HttpExchange exchange) throws IOException{
        try{
            AuthRequest authRequest = getDTO(exchange, AuthRequest.class);

            Session createdSession = authService.register(authRequest.getUsername(), authRequest.getPassword());

            AuthResponse response = new AuthResponse(
                    createdSession.getToken()
            );

            String responseJson = JsonUtil.toJson(response);
            sendResponse(exchange,201, responseJson);
        }catch(ValidationException e){
            handleError("Validation error", e.getMessage(), 400, exchange);
        }catch(UserAlreadyExistsException e){
            handleError("User exists error", e.getMessage(), 409, exchange);
        }catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            handleError("Internal error", "An unexpected error occurred", 500, exchange);
        }
    }

    private void handleGetProfile(HttpExchange exchange, int requestUserId) throws IOException{
        try{
            String token = extractBearerToken(exchange);
            int loggedInUserId = authService.getLoggedInUser(token);
            if(loggedInUserId == -1 || loggedInUserId != requestUserId){
                handleError("Unauthorized", "User is not logged in", 401, exchange);
                return;
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

}
