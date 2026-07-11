package service;
import dataaccess.*;
import service.RR.*;

import java.util.ArrayList;

public class GameService {

    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public JoinResult joinGame(JoinRequest joinRequest) {
        return new JoinResult(299);
    }
    public CreateResult createGame(CreateRequest createRequest) {
        return new CreateResult(12345, 299);
    }
    public ListResult listGames(ListRequest listRequest) {
        return new ListResult(new ArrayList<>(), 299);
    }
}
