package client.websocket;

import com.google.gson.Gson;
import exceptions.ServerResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ServerResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);       //deserializes and passes right along to the notification handler
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }



    public void connect(String authToken, int gameID) throws ServerResponseException {
        try {
            ConnectCommand conn = new ConnectCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(conn));
        }
        catch (IOException e) {
            throw new ServerResponseException("Error: " + e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ServerResponseException {
        try {
            LeaveGameCommand leave = new LeaveGameCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leave));
        }
        catch (IOException e) {
            throw new ServerResponseException("Error: " + e.getMessage());
        }
    }

    public void resign(String auth, int gameId) throws ServerResponseException {
        try {
            ResignCommand resign = new ResignCommand(auth, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(resign));
        }
        catch (IOException e) {
            throw new ServerResponseException("Error: " + e.getMessage());
        }
    }

    public void move(String auth, int gameID, String moveFrom, String moveTo) throws ServerResponseException{
        try {
            MakeMoveCommand move = new MakeMoveCommand(auth, gameID, chessmove);
            this.session.getBasicRemote().sendText(new Gson().toJson(move));
        }
        catch (IOException e) {
            throw new ServerResponseException("Error: " + e.getMessage());
        }
    }

}
