package service;

import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import service.reqRes.*;


public class ClearServiceTest {

    private UserService us;
    private GameService gs;
    private ClearService cs;
    AuthDAOMemory authDao;
    UserDAOMemory userDao;
    GameDAOMemory gameDao;

    @BeforeEach
    public void setup() {
        authDao = new AuthDAOMemory();
        userDao = new UserDAOMemory();
        gameDao = new GameDAOMemory();
        us = new UserService(userDao, authDao);
        gs = new GameService(userDao, authDao, gameDao);
        cs =  new ClearService(gameDao, userDao, authDao);
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        us.register(new RegisterRequest("brady", "123", "me@mail.com"));
        String auth = us.register(new RegisterRequest("caroline", "apples56", "c@mail.com")).authToken();

        gs.createGame(new CreateRequest(auth, "game1"));
        gs.createGame(new CreateRequest(auth, "game2"));

        cs.clear(new ClearRequest());

        Assertions.assertEquals(us, new UserService(new UserDAOMemory(), new AuthDAOMemory()));
        Assertions.assertEquals(gs, new GameService(new UserDAOMemory(), new AuthDAOMemory(), new GameDAOMemory()));

    }

}
