package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.ServerResponseException;
import model.AbbreviatedGame;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;
import service.reqres.ClearRequest;
import service.reqres.ListResult;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOTests {

    private AuthDAO authDao;
    private UserDAO userDao;
    private GameDAO dao;
    private ClearService clear;


    @BeforeEach
    public void setup() {
        dao = new GameDAOsql();

        authDao = new AuthDAOsql();
        userDao = new UserDAOsql();

        clear = new ClearService(dao, userDao, authDao);
        Assertions.assertDoesNotThrow(() -> clear.clear(new ClearRequest()));

    }

    @Test
    public void createGameSuccess() throws ServerResponseException {

        Assertions.assertDoesNotThrow(() -> dao.createGame("game1"));

        int id = dao.createGame("game2");

        Assertions.assertDoesNotThrow(() -> dao.getGame(id));
    }

    @Test
    public void createGameFail() {
        // ???
    }

    @Test
    public void joinGameSuccess() throws ServerResponseException {
        int id = dao.createGame("game1");

        //join as white
        dao.updateGame(dao.getGame(id), "WHITE", "username1");

        GameData expected = new GameData(id, "username1", null, "game1", new ChessGame());
        GameData actual = dao.getGame(id);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void joinGameFail() throws ServerResponseException {

        //invalid game id does not update
        GameData game = new GameData(8, null, "username2", "fakeGameName", new ChessGame());

        dao.updateGame(game, "WHITE", "myUsername");

        Assertions.assertNull(dao.getGame(8));

    }

    @Test
    public void getGameSuccess() throws ServerResponseException {
        int id1 = dao.createGame("game1");
        int id2 = dao.createGame("game2");
        int id3 = dao.createGame("game3");

        Assertions.assertDoesNotThrow(() -> dao.getGame(id1));
        Assertions.assertDoesNotThrow(() -> dao.getGame(id2));
        Assertions.assertDoesNotThrow(() -> dao.getGame(id3));

        GameData expected = new GameData(id1, null, null, "game1", new ChessGame());
        GameData actual = dao.getGame(id1);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void getGameFail() throws ServerResponseException {
        Assertions.assertNull(dao.getGame(8));
    }

    @Test
    public void listGamesSuccess() throws ServerResponseException {
        int id1 = dao.createGame("game1");
        int id2 = dao.createGame("game2");
        int id3 = dao.createGame("game3");

        AbbreviatedGame g1 = new AbbreviatedGame(id1, null, null, "game1");
        AbbreviatedGame g2 = new AbbreviatedGame(id2, null, null, "game2");
        AbbreviatedGame g3 = new AbbreviatedGame(id3, null, null, "game3");
        Collection<AbbreviatedGame> expected = new ArrayList<>();
        expected.add(g1);
        expected.add(g2);
        expected.add(g3);

        Collection<AbbreviatedGame> actual = dao.listGames();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void listGamesFail() throws ServerResponseException {
        Assertions.assertEquals(new ArrayList<AbbreviatedGame>(), dao.listGames());
    }

    @Test
    public void clearGames() throws ServerResponseException {
        dao.createGame("game1");
        dao.createGame("game2");
        dao.createGame("game3");

        dao.clearGames();

        Collection<AbbreviatedGame> expected = new ArrayList<>();
        Assertions.assertEquals(expected, dao.listGames());
    }



}
