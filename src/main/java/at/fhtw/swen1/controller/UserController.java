package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.RegisterRequest;
import at.fhtw.swen1.dto.RegisterResponse;
import at.fhtw.swen1.model.User;
import at.fhtw.swen1.service.UserService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class UserController implements HttpHandler {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if(path.equals("/api/users/register") && method.equals("POST")){
            handleRegister(exchange);
        }
        else{
            sendResponse(exchange, 404,  "{\"error\":\"Not found\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException{
        try{
            InputStream requestBody = exchange.getRequestBody();
            String json = new String(requestBody.readAllBytes());
            RegisterRequest registerRequest = JsonUtil.fromJson(json, RegisterRequest.class);

            User user = userService.register(registerRequest.getUsername(), registerRequest.getPassword());

            RegisterResponse response = new RegisterResponse(
                    user.getId(),
                    user.getUsername(),
                    "User registered successfully"
            );

            String responseJson = JsonUtil.toJson(response);
            sendResponse(exchange,201, responseJson);
        }catch(IllegalArgumentException e){
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }catch(Exception e){
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException{
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseJson.getBytes().length);
        exchange.getResponseBody().write(responseJson.getBytes());
        exchange.close();
    }
}
