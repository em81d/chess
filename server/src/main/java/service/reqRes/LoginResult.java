package service.reqRes;

public record LoginResult(String username, String authToken, int status) {
}
