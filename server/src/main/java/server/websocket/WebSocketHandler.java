package server.websocket;

import com.google.gson.Gson;
import com.mysql.cj.protocol.a.BooleanValueEncoder;
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

    private final ConnectionManager connections = new ConnectionManager();

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
            String username = getUsername(cmd.getAuthToken());          //how???
            saveSession(gameId, session);       //adding to connection manager

            switch (cmd.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) cmd);       //make the other three functions
                case MAKE_MOVE -> move(session, username, (MakeMoveCommand) cmd);       //still need to deserialize the move
                //deserialize the whole cmd as a MakeMoveCommand instead of a UserGameCommand
                case LEAVE -> leave(session, username, (LeaveGameCommand) cmd);
                case RESIGN -> resign(session, username, (ResignCommand) cmd);
            }
        }
        catch (NoAuthException ex) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));  //will this ever get thrown?
        }
        catch (IOException ex) {
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
    private void connect(Session session, String username, ConnectCommand cmd) throws IOException {
        connections.add(session);       //update bc data structure changed
        var message = String.format("%s has joined the game.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification);   //update
    }
}