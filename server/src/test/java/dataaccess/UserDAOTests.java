package dataaccess;

import dataaccess.exceptions.ServerResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.reqres.ClearRequest;


public class UserDAOTests {

    private AuthDAO authDao;
    private UserDAO dao;
    private GameDAO gameDao;
    private ClearService clear;


    @BeforeEach
    public void setup() {
        dao = new UserDAOsql();

        authDao = new AuthDAOsql();
        gameDao = new GameDAOsql();

        clear = new ClearService(gameDao, dao, authDao);
        Assertions.assertDoesNotThrow(() -> clear.clear(new ClearRequest()));

    }


    @Test
    public void testCreateUserSuccess() {
        UserData user = new UserData("user1", "password1", "user@mail.com");
        Assertions.assertDoesNotThrow(() -> dao.createUser(user));
    }

    @Test
    public void testCreateUserFail() throws ServerResponseException {
        UserData user1 = new UserData("user1", "password1", "user@mail.com");
        dao.createUser(user1);
        //creating user with duplicate username
        UserData user2 = new UserData("user1", "password2", "user2@mail.com");
        Assertions.assertThrows(ServerResponseException.class, () -> dao.createUser(user2));
    }

    @Test
    public void getUserSuccess() throws ServerResponseException {
        UserData user1 = new UserData("user1", "password1", "user@mail.com");
        dao.createUser(user1);

        UserData expected = new UserData("user1", "password1", "user@mail.com");
        UserData actual = dao.getUser("user1");

        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertNotEquals(expected.password(), actual.password());
        Assertions.assertEquals(expected.email(), actual.email());

    }

    @Test
    public void getUserFail() throws ServerResponseException {
         Assertions.assertNull(dao.getUser("nonexistentUsername"));

    }

    @Test
    public void clearUserSuccess() throws ServerResponseException {
        dao.createUser(new UserData("user1", "password1", "email1"));
        dao.createUser(new UserData("user2", "password2", "email2"));
        dao.createUser(new UserData("user3", "password3", "email3"));

        dao.clearUsers();

        Assertions.assertNull(dao.getUser("user1"));
        Assertions.assertNull(dao.getUser("user2"));
        Assertions.assertNull(dao.getUser("user3"));

    }


}
