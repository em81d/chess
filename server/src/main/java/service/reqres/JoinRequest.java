package service.reqres;

public record JoinRequest(String authToken, String color, int gameID) {
}
