package service;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import service.RR.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;

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
        String authToken = createRequest.authToken();
        try {
            authDao.getAuth(authToken);
            int id = gameDao.createGame(createRequest.gameName());
            return new CreateResult(id, 200);
        }
        catch (DataAccessException e) {
            return new CreateResult(-1, 401);
        }

    }
    public ListResult listGames(ListRequest listRequest) { //do I throw the data access exception back to the server or handle it here by returning a failure status code?
        String authToken = listRequest.authToken();
        try {
            authDao.getAuth(authToken);
            return new ListResult(gameDao.listGames(), 200);
        }
        catch (DataAccessException e){
            return new ListResult(null, 401);
        }

    }
}
