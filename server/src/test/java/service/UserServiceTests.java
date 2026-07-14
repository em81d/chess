package service;

import dataaccess.AuthDAOMemory;
import dataaccess.UserDAOMemory;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.NoAuthException;
import org.junit.jupiter.api.*;
import service.reqRes.*;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTests {

    private UserService us;
    AuthDAOMemory authDao;
    UserDAOMemory userDao;

    @BeforeEach
    public void setup() {
        authDao = new AuthDAOMemory();
        userDao = new UserDAOMemory();
        us = new UserService(userDao, authDao);
    }

    @Test
    public void testRegisterSuccess() throws DataAccessException {

        RegisterRequest req = new RegisterRequest("myName", "myPassword", "my@email.com");
        RegisterResult actual = us.register(req);
        RegisterResult expected = new RegisterResult("myName", "authToken",  200);
        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.status(), actual.status());
    }

    @Test
    public void testRegisterFail() throws DataAccessException {
        us.register(new RegisterRequest("emeline", "password", "email@email.com"));
        RegisterRequest req = new RegisterRequest("emeline", "akhdgifbnvdai", "myEmail");
        assertThrows(AlreadyTakenException.class, () -> us.register(req));
    }

    @Test
    public void testLoginSuccess() throws DataAccessException {
        String auth = us.register(new RegisterRequest("maria", "angel25", "maria@mail.com")).authToken();
        authDao.deleteAuth(auth);

        LoginResult expected = new LoginResult("maria", "token", 200);
        LoginResult actual = us.login(new LoginRequest("maria", "angel25"));

        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.status(), actual.status());

    }

    @Test
    public void testLoginFail() throws DataAccessException {
        String auth = us.register(new RegisterRequest("maria", "angel25", "maria@mail.com")).authToken();
        authDao.deleteAuth(auth);

        assertThrows(NoAuthException.class, () -> us.login(new LoginRequest("maria", "wrongPassword")));
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        String auth = us.register(new RegisterRequest("amy", "ilovepuppies", "amy@mail.com")).authToken();

        LogoutRequest req = new LogoutRequest(auth);

        LogoutResult expected = new LogoutResult(200);
        LogoutResult actual = us.logout(req);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testLogoutFail() throws DataAccessException {
        String auth = us.register(new RegisterRequest("amy", "ilovepuppies", "amy@mail.com")).authToken();

        LogoutRequest req = new LogoutRequest("notTheRightAuthToken");

        assertThrows(NoAuthException.class, () -> us.logout(req));
    }

}
