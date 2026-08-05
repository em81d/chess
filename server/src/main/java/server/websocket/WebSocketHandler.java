package server.websocket;

import com.google.gson.Gson;
import com.mysql.cj.protocol.a.BooleanValueEncoder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exceptions.NoAuthException;
import exceptions.ServerResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.*;


import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public WebSocketHandler(AuthDAO auth, GameDAO game) {
        connections = new ConnectionManager();
        authDao = auth;
        gameDao = game;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception{
        int gameId = -1;
        Session session = ctx.session;
        try {
            UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameId = cmd.getGameID();
            String auth = cmd.getAuthToken();
            String username = authDao.getAuth(auth).username();

            switch (cmd.getCommandType()) {
                case CONNECT -> connect(gameId, session, username);       //make the other three functions
                case MAKE_MOVE -> move(gameId, session, username, (MakeMoveCommand) cmd);       //still need to deserialize the move
                //deserialize the whole cmd as a MakeMoveCommand instead of a UserGameCommand
                case LEAVE -> leave(gameId, session, username);
                case RESIGN -> resign(gameId, session, username);
            }
        }
        catch (ServerResponseException ex) {
            connections.sendToSession(gameId, session, new ErrorMessage("Error: unauthorized"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    //pet shop methods
    //incoming messages
//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }
//
//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
//
         //outgoing message from server
//    public void makeNoise(String petName, String sound) throws ServerResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ServerResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }


    //chess methods
    private void connect(int gameID, Session session, String username) throws IOException {
        connections.add(gameID, session);
        var message = String.format("%s has joined the game.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
        try {

            connections.sendToSession(gameID, session, new LoadGameMessage(gameDao.getGame(gameID)));
        }
        catch (ServerResponseException e) {
            connections.broadcast(gameID, null, new NotificationMessage("Error loading game associated with " + gameID));
        }

    }

    private void move(int gameID, Session session, String username, MakeMoveCommand cmd) {

    }

    private void leave(int gameID, Session session, String username) {

    }

    private void resign(int gameID, Session session, String username) {

    }
}