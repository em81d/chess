package client;
import exceptions.ServerResponseException;
import reqres.*;

import com.google.gson.Gson;

import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.*;
import java.util.Map;


public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest req) throws ServerResponseException {
        HttpRequest request = buildRequest("POST", "/user", req, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);

    }

    public LoginResult login(LoginRequest req) throws ServerResponseException {
        HttpRequest request = buildRequest("POST", "/session", req, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest req) throws ServerResponseException {
        HttpRequest request = buildRequest("DELETE", "/session", null, req.authToken());
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public CreateResult create(CreateRequest req) throws ServerResponseException {
        //probably need to not pass the whole req object here. hoW!
        HttpRequest request = buildRequest("POST", "/game", Map.of("gameName", req.gameName()), req.authToken());
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateResult.class);

    }

    public JoinResult join(JoinRequest req) throws ServerResponseException {
        Map<String, Object> body = Map.of("playerColor", req.color(), "gameID", req.gameID());
        HttpRequest request = buildRequest("PUT", "/game", body, req.authToken());
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, JoinResult.class);

    }

    public ListResult listGames(ListRequest req) throws ServerResponseException {
        HttpRequest request = buildRequest("GET", "/game", null, req.authToken());
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, ListResult.class);

    }

    public ClearResult clear(ClearRequest req) throws ServerResponseException {
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, ClearResult.class);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null) {
            request.setHeader("authorization", header);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServerResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ServerResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ServerResponseException(body);
            }

            throw new ServerResponseException("Error: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
