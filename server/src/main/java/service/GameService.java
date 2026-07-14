package service;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.NoAuthException;
import model.AuthData;
import model.GameData;
import service.RR.*;

public class GameService {

    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public JoinResult joinGame(JoinRequest joinRequest) throws DataAccessException {
        String authToken = joinRequest.authToken();
        AuthData auth = authDao.getAuth(authToken);
        if (auth == null) {
            throw new NoAuthException("invalid auth token");
        }
        //should give status 401 - unauthorized

        int gameID = joinRequest.gameID();
        GameData game = gameDao.getGame(gameID);
        if (game == null || joinRequest.color() == null
            || (!joinRequest.color().equals("WHITE") && !joinRequest.color().equals("BLACK"))) {
            throw new BadRequestException("invalid gameID or team color");
        }
        //should give status 400 - bad request


        if (joinRequest.color().equals("WHITE") && game.whiteUsername()==null) {
            gameDao.updateGame(game, joinRequest.color(), auth.username());
        }
        else if (joinRequest.color()=="BLACK" && game.blackUsername()==null) {
            gameDao.updateGame(game, joinRequest.color(), auth.username());
        }
        else {
            //already taken exception  - status 403
            throw new AlreadyTakenException("spot in game already taken by a user");
        }

        return new JoinResult(200);
    }

    public CreateResult createGame(CreateRequest createRequest) throws DataAccessException {
        String authToken = createRequest.authToken();
//        System.out.println("auth token: " + authToken);

        if (authDao.getAuth(authToken) == null) {
            throw new NoAuthException("invalid auth token.");
            //401 unauthorized
        }
        if (createRequest.gameName() == null) {
            //400 bad request
            //I don't think that we need to check whether game name is already taken. game name shouldn't need to
            //be unique since we have the game id
            throw new BadRequestException("no game name");
        }
        int id = gameDao.createGame(createRequest.gameName());
        return new CreateResult(id, 200);
    }

    //list games eventually needs to return the games in the format specified rather than printing out the whole game board
    public ListResult listGames(ListRequest listRequest) throws NoAuthException {
        String authToken = listRequest.authToken();
        if (authToken == null || authDao.getAuth(authToken) == null) {
            throw new NoAuthException("invalid auth token or no auth token");
            //401 unauthorized
        }

        return new ListResult(gameDao.listGames(), 200);
    }
}
