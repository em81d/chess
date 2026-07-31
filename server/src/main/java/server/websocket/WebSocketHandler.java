package server.websocket;

import com.google.gson.Gson;
import com.mysql.cj.protocol.a.BooleanValueEncoder;
import exceptions.ServerResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
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
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (cmd.getCommandType()) {
                case CONNECT -> connect(cmd.getAuthToken(), ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMove = (MakeMoveCommand) cmd;
                    move(cmd.getAuthToken(), ctx.session, makeMove.getMove());
                }
                case LEAVE -> leave(cmd.getAuthToken(), ctx.session);
                case RESIGN -> resign(cmd.getAuthToken(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    //pet shop methods
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
    private void connect(String auth, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s has joined the game.", auth); //how do I get the user's name from here???
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification);
    }
}