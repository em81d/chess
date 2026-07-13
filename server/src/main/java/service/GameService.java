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
        String authToken = joinRequest.authToken();
        AuthData auth;
        try {
            auth = authDao.getAuth(authToken);
        }
        catch (DataAccessException e) {
            //unauthorized - no auth token
            return new JoinResult(401);
        }

        int gameID = joinRequest.gameID();
        GameData game;
        try  {
            game = gameDao.getGame(gameID);
        }
        catch (DataAccessException e){
            //bad request - game with that id does not exist
            return new JoinResult(400);
        }
        //also needs to return a 400 if no player color
        if (joinRequest.color()!=null && (joinRequest.color()=="WHITE" || joinRequest.color()=="BLACK")) {
            return new JoinResult(400);
        }

        try {
            if (joinRequest.color()=="WHITE" && game.whiteUsername()==null) {
                gameDao.updateGame(joinRequest.gameID(), joinRequest.color(), auth.username());
            }
            else if (joinRequest.color()=="BLACK" && game.blackUsername()==null) {
                gameDao.updateGame(joinRequest.gameID(), joinRequest.color(), auth.username());
            }
            else {
                //already taken
                return new JoinResult(403);
            }
        }
        catch (DataAccessException e){
            //this should be unnecessary but I think it has to catch it since update game throws it
            return new JoinResult(400);
        }

        return new JoinResult(200);
    }

    public CreateResult createGame(CreateRequest createRequest) {
        String authToken = createRequest.authToken();
//        System.out.println("auth token: " + authToken);
        try {
            authDao.getAuth(authToken);
            int id = gameDao.createGame(createRequest.gameName());
            return new CreateResult(id, 200);
        }
        catch (DataAccessException e) {
            return new CreateResult(-1, 401);
        }

    }

    //list games eventually needs to return the games in the format specified rather than printing out the whole game board
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
