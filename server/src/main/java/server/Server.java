package server;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataaccess.*;
import exceptions.*;
import io.javalin.*;
import io.javalin.http.Context;
import reqres.*;
import server.websocket.WebSocketHandler;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    private final WebSocketHandler wsHandler;

    public Server() {

        userDao = new UserDAOMemory();
        gameDao = new GameDAOMemory();
        authDao = new AuthDAOMemory();

        wsHandler = new WebSocketHandler(authDao, gameDao);


        userService = new UserService(userDao, authDao);
        gameService = new GameService(userDao, authDao, gameDao);
        clearService = new ClearService(gameDao, userDao, authDao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clear)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::list)
                .post("/game", this::create)
                .put("/game", this::join)
                .error(404, this::notFound)
                .exception(BadRequestException.class, this::badRequest)
                .exception(AlreadyTakenException.class, this::alreadyTaken)
                .exception(NoAuthException.class, this::noAuth)
                .exception(ServerResponseException.class, this::serverError)
                .ws("/ws", ws -> {
                    ws.onConnect(wsHandler);
                    ws.onMessage(wsHandler);
                    ws.onClose(wsHandler);
                });

        // Register your endpoints and exception handlers here.

    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }


    private int port() {
        return javalin.port();
    }

    private void register(Context context) throws DataAccessException {
        //deserialize
        RegisterRequest req = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult res = userService.register(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }

    private void clear(Context context) throws ServerResponseException {
//        deserialize
        ClearRequest req = new Gson().fromJson(context.body(), ClearRequest.class);
        ClearResult res = clearService.clear(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }

    private void login(Context context) throws DataAccessException {
        //deserialize
        LoginRequest req = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult res = userService.login(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }

    private void logout(Context context) throws DataAccessException {
        //deserialize
        LogoutRequest req = new LogoutRequest(context.header("authorization"));
        LogoutResult res = userService.logout(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }

    private void create(Context context) throws DataAccessException {
        String authToken = context.header("authorization");

        //getting the game name
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);
        JsonElement gameName = json.get("gameName");
        if (gameName==null) {
            throw new BadRequestException("game name cannot be empty");
        }
        else {
            CreateRequest req = new CreateRequest(authToken, gameName.getAsString());
            CreateResult res = gameService.createGame(req);
            context.status(res.status());
            context.result(new Gson().toJson(res));
        }
    }

    private void list(Context context) throws DataAccessException {
        //deserialize
        ListRequest req = new ListRequest(context.header("authorization"));
//        ListRequest req = new Gson().fromJson(context.header("authorization"), ListRequest.class);
        ListResult res = gameService.listGames(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }

    private void join(Context context) throws DataAccessException {
        //deserialize
        String authToken = context.header("authorization");
        JsonObject json = new Gson().fromJson(context.body(), JsonObject.class);

        if (json.get("gameID") == null || json.get("playerColor") == null) {
            throw new BadRequestException("must include a game ID and player color");
        }

        int gameID = json.get("gameID").getAsInt();
        String color = json.get("playerColor").getAsString();

        JoinRequest req = new JoinRequest(authToken, color, gameID);
        JoinResult res = gameService.joinGame(req);
        context.status(res.status());
        context.result(new Gson().toJson(res));
    }


    //error handlers
    private void alreadyTaken(AlreadyTakenException e, Context context) {
        context.status(403);
        context.result(e.toJson());
    }

    private void noAuth(NoAuthException e, Context context) {
        context.status(401);
        context.result(e.toJson());
    }

    private void badRequest(BadRequestException e, Context context) {
        context.status(400);
        context.result(e.toJson());
    }

    private void serverError(ServerResponseException e, Context context) {
        context.status(500);
        context.result(e.toJson());
    }

    private void notFound(Context context) {
        context.status(404);
    }


    //needs to receive HTTP requests and INITIALIZE all the handlers
    //when a request comes in, it checks what kind it is, and forwards it to the appropriate handler
    //handler deals with the http communication details
    //so the logout handler would receive the http request for a user to logout and convert it to a
    //LogoutRequest object that gets passed on to the service
    //so handler is the one that parses the json
    //when the handler receives the logout result, it converts it back into json and passes back to the server

    public void stop() {
        javalin.stop();
    }
}

