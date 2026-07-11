package service;

import service.RR.*;

import java.util.ArrayList;

public class GameService {

    public JoinResult joinGame(JoinRequest joinRequest) {
        return new JoinResult(200);
    }
    public CreateResult createGame(CreateRequest createRequest) {
        return new CreateResult(12345, 200);
    }
    public ListResult listGames(ListRequest listRequest) {
        return new ListResult(new ArrayList<>(), 200);
    }
}
