package server;
import com.google.gson.Gson;
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

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);


        // Register your endpoints and exception handlers here.
        userService = new UserService();


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

