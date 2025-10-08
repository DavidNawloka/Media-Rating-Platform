package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.LoginRequest;
import at.fhtw.swen1.dto.AuthResponse;
import at.fhtw.swen1.dto.RegisterRequest;
import at.fhtw.swen1.exception.CredentialsException;
import at.fhtw.swen1.exception.UserAlreadyExistsException;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.model.Session;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;


public class AuthController extends Controller {
    private final AuthService authService;

    public AuthController( AuthService authService) {
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

        handleError("Not found", "Incorrect path", 404, exchange);
    }

    private void handleLogin(HttpExchange exchange) throws IOException{
        try{
            LoginRequest loginRequest = getDTO(exchange, LoginRequest.class);

            Session session = authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

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
            RegisterRequest registerRequest = getDTO(exchange, RegisterRequest.class);

            Session createdSession = authService.register(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());

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



}
