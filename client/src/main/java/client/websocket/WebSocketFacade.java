package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.ServerResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import static chess.ChessPiece.PieceType.*;

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
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);

                    switch (notification.getServerMessageType()) {
                        case ERROR -> notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                        case LOAD_GAME -> notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                    }

//                    notificationHandler.notify(notification);       //deserializes and passes right along to the notification handler
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

    public void move(String auth, int gameID, String moveFrom, String moveTo, String promotion) throws ServerResponseException{
        try {

            ChessMove chessmove = new ChessMove(toPosition(moveFrom), toPosition(moveTo), toPromotion(promotion));

            MakeMoveCommand m = new MakeMoveCommand(auth, gameID, chessmove);
            this.session.getBasicRemote().sendText(new Gson().toJson(m));
//            System.out.println("successfully ran this method.");
        }
        catch (IOException e) {
            throw new ServerResponseException("Error: " + e.getMessage());
        }
    }

    public ChessPiece.PieceType toPromotion(String promotionString){
        if (promotionString == null) {
            return null;
        }
        if (promotionString.equalsIgnoreCase("bishop")) {
            return BISHOP;
        }
        if (promotionString.equalsIgnoreCase("queen")) {
            return QUEEN;
        }
        if (promotionString.equalsIgnoreCase("knight")) {
            return KNIGHT;
        }
        if (promotionString.equalsIgnoreCase("rook")) {
            return ROOK;
        }
        return null;
    }

    public ChessPosition toPosition(String posString) {
        int i;
        int j;

        switch (posString.substring(0,1)) {
            case "a" -> j = 1;
            case "b" -> j = 2;
            case "c" -> j = 3;
            case "d" -> j = 4;
            case "e" -> j = 5;
            case "f" -> j = 6;
            case "g" -> j = 7;
            case "h" -> j = 8;
            default -> j = -1;
        }
        i = Character.getNumericValue(posString.charAt(1));

        return new ChessPosition(i,j);
    }

}
