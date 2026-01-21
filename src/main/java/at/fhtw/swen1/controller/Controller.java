package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.ErrorResponse;
import at.fhtw.swen1.exception.ValidationException;
import at.fhtw.swen1.service.AuthService;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller implements HttpHandler {


    protected final AuthService authService;

    public Controller(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected <T> T getDTO(HttpExchange exchange, Class<T> expectedDTO) throws IOException, ValidationException {
        InputStream requestBody = exchange.getRequestBody();
        String json = new String(requestBody.readAllBytes());
        return JsonUtil.fromJson(json, expectedDTO);
    }

    protected String extractBearerToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7); // Remove "Bearer " prefix
    }

    protected void handleError(String error, String errorMessage, int code, HttpExchange exchange) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(error, errorMessage, code, System.currentTimeMillis());
        String responseJson = JsonUtil.toJson(errorResponse);
        sendResponse(exchange, code, responseJson);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseJson.getBytes().length);
        exchange.getResponseBody().write(responseJson.getBytes());
        exchange.close();
    }

    protected void sendResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
    }


    protected int getLoggedInUserId(HttpExchange exchange) throws IOException {
        String token = extractBearerToken(exchange);
        int loggedInUserId = authService.getLoggedInUser(token);
        if(loggedInUserId == -1 ){
            handleError("Unauthorized", "User is not logged in", 401, exchange);
        }
        return loggedInUserId;
    }

    protected Map<String, String> parseQueryParams(String query){
        Map<String, String> params = new HashMap<>();
        if(query == null) return params;

        for(String param: query.split("&")){
            String[] pair = param.split("=");
            if(pair.length == 2){
                params.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}
