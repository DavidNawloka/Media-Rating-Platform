package at.fhtw.swen1.controller;

import at.fhtw.swen1.dto.ErrorResponse;
import at.fhtw.swen1.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public abstract class Controller implements HttpHandler {


    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected <T> T getDTO(HttpExchange exchange, Class<T> expectedDTO) throws IOException {
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
}
