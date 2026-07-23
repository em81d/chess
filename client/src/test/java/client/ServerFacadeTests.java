package client;

import exceptions.ServerResponseException;
import model.AbbreviatedGame;
import org.junit.jupiter.api.*;
import reqres.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);

        //clear database first
        Assertions.assertDoesNotThrow(() -> facade.clear(new ClearRequest()));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerSuccess() throws ServerResponseException {
        RegisterResult res = facade.register(new RegisterRequest("user1", "password", "email"));
        Assertions.assertEquals(200, res.status());
    }

    @Test
    public void registerFail() throws ServerResponseException {
        //email is null
        Assertions.assertThrows(ServerResponseException.class, () -> facade.register(new RegisterRequest("user2", "password", null)));

        //duplicate user
        facade.register(new RegisterRequest("user2", "password", "email"));
        Assertions.assertThrows(ServerResponseException.class, () -> facade.register(new RegisterRequest("user2", "pweadkljhfa", "email")));

    }

    @Test
    public void loginSuccess() throws ServerResponseException {
        facade.register(new RegisterRequest("user2", "pwd", "email"));
        Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("user2", "pwd")));

        facade.register(new RegisterRequest("user3", "pwd", "email"));
        LoginResult res = facade.login(new LoginRequest("user3", "pwd"));

        Assertions.assertEquals(200, res.status());

    }

    @Test
    public void loginFail() {
        //wrong credentials
        LoginRequest req = new LoginRequest("user3", "wrongPassword");
        Assertions.assertThrows(ServerResponseException.class, () -> facade.login(req));
    }

    @Test
    public void logoutSuccess() throws ServerResponseException {
        // logout doesn't throw exception
        RegisterResult res = facade.register(new RegisterRequest("user4", "pwd", "email"));
        String auth = res.authToken();
        Assertions.assertDoesNotThrow(() -> facade.logout(new LogoutRequest(auth)));

    }

    @Test
    public void logoutFail() {
        // no auth
        Assertions.assertThrows(ServerResponseException.class, () -> facade.logout(new LogoutRequest(null)));

    }

    @Test
    public void createSuccess() throws ServerResponseException {
        String auth = facade.register(new RegisterRequest("user5", "alkdjhvdj", "email")).authToken();

        Assertions.assertDoesNotThrow(() -> facade.create(new CreateRequest(auth, "game1")));

    }

    @Test
    public void createFail() {
        //unauthorized
        Assertions.assertThrows(ServerResponseException.class, () -> facade.create(new CreateRequest(null, "game2")));
    }

    @Test
    public void joinSuccess() throws ServerResponseException {
        String auth = facade.register(new RegisterRequest("user6", "alkdjhvdj", "email")).authToken();

        int id = facade.create(new CreateRequest(auth, "game3")).gameID();
        JoinResult res = facade.join(new JoinRequest(auth, "BLACK", id));

        Assertions.assertEquals(new JoinResult(200), res);
    }

    @Test
    public void joinFail() throws ServerResponseException {
        String auth = facade.register(new RegisterRequest("user7", "alkdjhvdj", "email")).authToken();

        //invalid game id
        Assertions.assertThrows(ServerResponseException.class, () -> facade.join(new JoinRequest(auth, "WHITE", 300)));

        //invalid color
        Assertions.assertThrows(ServerResponseException.class, () -> facade.join(new JoinRequest(auth, "PURPLE", 1)));

        //no auth
        Assertions.assertThrows(ServerResponseException.class, () -> facade.join(new JoinRequest(null, "WHITE", 1)));
    }

    @Test
    public void listSuccess() throws ServerResponseException {
        facade.clear(new ClearRequest());

        String auth = facade.register(new RegisterRequest("user8", "alkdjhvdj", "email")).authToken();

        ListResult res = facade.listGames(new ListRequest(auth));
        Assertions.assertEquals(new ArrayList<AbbreviatedGame>(), res.games());

        facade.create(new CreateRequest(auth, "myGame1"));
        facade.create(new CreateRequest(auth, "myGame2"));
        facade.create(new CreateRequest(auth, "myGame3"));

        Collection<AbbreviatedGame> actual = facade.listGames(new ListRequest(auth)).games();
        Collection<AbbreviatedGame> expected = new ArrayList<>();
        expected.add(new AbbreviatedGame(1, null, null, "myGame1"));
        expected.add(new AbbreviatedGame(2, null, null, "myGame2"));
        expected.add(new AbbreviatedGame(3, null, null, "myGame3"));

        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void listFail() {
        // no auth
        Assertions.assertThrows(ServerResponseException.class, () -> facade.listGames(new ListRequest(null)));

    }

    @Test
    public void clearSuccess() throws ServerResponseException {
        facade.register(new RegisterRequest("user9", "p", "e"));
        facade.register(new RegisterRequest("user10", "p", "e"));
        String auth = facade.register(new RegisterRequest("user11", "p", "e")).authToken();

        facade.create(new CreateRequest(auth, "game11"));
        facade.create(new CreateRequest(auth, "game12"));
        facade.create(new CreateRequest(auth, "game13"));

        facade.clear(new ClearRequest());

        //check that users were deleted
        Assertions.assertThrows(ServerResponseException.class, () -> facade.login(new LoginRequest("user9", "p")));

        //check that games were deleted
        auth = facade.register(new RegisterRequest("user9", "p", "e")).authToken();
        Assertions.assertEquals(new ArrayList<AbbreviatedGame>(), facade.listGames(new ListRequest(auth)).games());
    }

}
