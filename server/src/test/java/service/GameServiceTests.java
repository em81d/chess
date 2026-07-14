package service;

import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.NoAuthException;
import model.AbbreviatedGame;
import org.junit.jupiter.api.*;
import service.reqRes.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTests {

    private GameService gs;
    AuthDAOMemory authDao;
    UserDAOMemory userDao;
    GameDAOMemory gameDao;

    @BeforeEach
    public void setup() {
        authDao = new AuthDAOMemory();
        userDao = new UserDAOMemory();
        gameDao = new GameDAOMemory();
        gs = new GameService(userDao, authDao, gameDao);
    }

    @Test
    public void testCreateSuccess() throws DataAccessException {
        String token = authDao.createAuth("user1");
        CreateRequest req = new CreateRequest(token, "myGame");
        CreateResult actual = gs.createGame(req);
        CreateResult expected = new CreateResult(actual.gameID(), 200);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testCreateFail() {
        CreateRequest req = new CreateRequest(null, "myGame");
        assertThrows(NoAuthException.class, () -> gs.createGame(req));
    }

    @Test
    public void testListSuccess() throws DataAccessException {
        String token = authDao.createAuth("user1");
        gs.createGame(new CreateRequest(token, "game1"));
        gs.createGame(new CreateRequest(token, "game2"));
        gs.createGame(new CreateRequest(token, "game3"));

        ListResult actual = gs.listGames(new ListRequest(token));

        ArrayList<AbbreviatedGame> games = new ArrayList<>();
        games.add(new AbbreviatedGame(100, null, null, "game1"));
        games.add(new AbbreviatedGame(101, null, null, "game2"));
        games.add(new AbbreviatedGame(102, null, null, "game3"));
        ListResult expected = new ListResult(games, 200);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testListFail() {
        ListRequest req = new ListRequest(null);
        assertThrows(NoAuthException.class, () -> gs.listGames(req));
    }

    @Test
    public void testJoinSuccess() throws DataAccessException {
        String token = authDao.createAuth("user1");
        gs.createGame(new CreateRequest(token, "game1"));

        JoinResult actual = gs.joinGame(new JoinRequest(token, "WHITE", 100));
        JoinResult expected = new JoinResult(200);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testJoinFail() throws DataAccessException {
        String token = authDao.createAuth("user2");
        gs.createGame(new CreateRequest(token, "game2"));
        gs.joinGame(new JoinRequest(token, "WHITE", 100));


        JoinRequest req = new JoinRequest(token, "WHITE", 100);
        assertThrows(AlreadyTakenException.class, () -> gs.joinGame(req));
    }

}
