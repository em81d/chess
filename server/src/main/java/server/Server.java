package server;
import com.google.gson.Gson;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.RR.*;
import service.ClearService;
import service.UserService;
import service.GameService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        userService = new UserService(new UserDAOMemory(), new AuthDAOMemory());
        gameService = new GameService(new UserDAOMemory(), new AuthDAOMemory(), new GameDAOMemory());
        clearService = new ClearService(new GameDAOMemory(), new AuthDAOMemory(), new UserDAOMemory());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clear)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::list)
                .post("/game", this::create)
                .put("/game", this::join);


        // Register your endpoints and exception handlers here.



    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }


    private int port() {
        return javalin.port();
    }

    private void register(Context context) {
        //deserialize
        RegisterRequest req = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult res = userService.register(req);
        context.result(new Gson().toJson(res));
    }

    private void clear(Context context) {
//        deserialize
        ClearRequest req = new Gson().fromJson(context.body(), ClearRequest.class);
        ClearResult res = clearService.clear(req);
        context.result(new Gson().toJson(res));
    }

    private void login(Context context) {
        //deserialize
        LoginRequest req = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult res = userService.login(req);
        context.result(new Gson().toJson(res));
    }

    private void logout(Context context) {
        //deserialize
        LogoutRequest req = new Gson().fromJson(context.body(), LogoutRequest.class);
        LogoutResult res = userService.logout(req);
        context.result(new Gson().toJson(res));
    }

    private void create(Context context) {
        //deserialize
        CreateRequest req = new Gson().fromJson(context.body(), CreateRequest.class);
        CreateResult res = gameService.createGame(req);
        context.result(new Gson().toJson(res));
    }

    private void list(Context context) {
        //deserialize
        ListRequest req = new Gson().fromJson(context.body(), ListRequest.class);
        ListResult res = gameService.listGames(req);
        context.result(new Gson().toJson(res));
    }

    private void join(Context context) {
        //deserialize
        JoinRequest req = new Gson().fromJson(context.body(), JoinRequest.class);
        JoinResult res = gameService.joinGame(req);
        context.result(new Gson().toJson(res));
    }


    //needs to receive HTTP requests and INITIALIZE all the handlers
    //when a request comes in, it checks what kind it is, and forwards it to the appropriate handler
    //handler deals with the http communication details
    //so the logout handler would receive the http request for a user to logout and convert it to a LogoutRequest object that gets passed on to the service
    //so handler is the one that parses the json
    //when the handler receives the logout result, it converts it back into json and passes back to the server

    public void stop() {
        javalin.stop();
    }
}

