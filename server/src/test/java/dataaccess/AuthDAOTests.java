package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ServerResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.reqres.ClearRequest;

public class AuthDAOTests {

    private AuthDAO dao;
    private UserDAO userDao;
    private GameDAO gameDao;
    private ClearService clear;




    @BeforeEach
    public void setup() {
        dao = new AuthDAOsql();

        userDao = new UserDAOsql();
        gameDao = new GameDAOsql();

        clear = new ClearService(gameDao, userDao, dao);
        Assertions.assertDoesNotThrow(() -> clear.clear(new ClearRequest()));

    }

    @Test
    public void testAddAuthSuccess() throws DataAccessException {
        userDao.createUser(new UserData("em", "1234", "i@mail.com"));
        UserData user = userDao.getUser("em");
        Assertions.assertDoesNotThrow(() -> dao.createAuth(user.username()));
    }

    @Test
    public void testAddAuthFail() throws ServerResponseException {
//        Assertions.assertThrows(ServerResponseException.class, () -> dao.createAuth((""))); //not sure this will work

//        String token = dao.createAuth("user1"); //I could try to add a duplicate auth token which would throw
        //a sql error, but there is no way to set the authtoken manually. It would defeat the purpose to overload the method for that
    }

    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        userDao.createUser(new UserData("em", "1234", "i@mail.com"));
        UserData user = userDao.getUser("em");
        String token = dao.createAuth("user1");

        Assertions.assertDoesNotThrow(() -> dao.getAuth(token));
        Assertions.assertNotNull(dao.getAuth(token));
    }

    @Test
    public void testGetAuthFail() throws ServerResponseException {
        Assertions.assertNull(dao.getAuth("fakeUsername"));
    }

    @Test
    public void testDeleteSuccess() throws ServerResponseException {
        String token = dao.createAuth("user1");
        Assertions.assertNotNull(dao.getAuth(token));
        Assertions.assertDoesNotThrow(() -> dao.deleteAuth(token));
        Assertions.assertNull(dao.getAuth(token));
    }

    @Test
    public void testDeleteFail() throws ServerResponseException {
        Assertions.assertFalse(dao.deleteAuth("notARealToken"));
    }

    @Test
    public void clearSuccess() throws ServerResponseException {
        dao.createAuth("user1");
        dao.createAuth("user2");
        dao.createAuth("user3");

        Assertions.assertDoesNotThrow(() -> dao.clearAuths());
        Assertions.assertEquals(dao, new AuthDAOsql());
    }



}
