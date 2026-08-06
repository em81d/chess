package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;


import java.io.IOException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
            if (authDao.getAuth(auth) == null) {
                throw new NoAuthException("Error: unauthorized");
            }

            String username = authDao.getAuth(auth).username();

            switch (cmd.getCommandType()) {
                case CONNECT -> {
//                    connections.sendToSession(gameId, session, new NotificationMessage("will connect"));
                    connect(gameId, session, username);
                }
                case MAKE_MOVE -> {
//                    connections.sendToSession(gameId, session, new NotificationMessage("will move"));
                    move(gameId, session, username, ctx.message());
//                    connections.sendToSession(gameId, session, new NotificationMessage("move command executed"));
                }
                case LEAVE -> leave(gameId, session, username);
                case RESIGN -> resign(gameId, session, username);
            }
        }
        catch (NoAuthException ex) {
            connections.sendToSession(gameId, session, new ErrorMessage("Error: unauthorized"));
        }
        catch (ServerResponseException e) {
            connections.sendToSession(gameId, session, new ErrorMessage("Error: other server error"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }


    //chess methods
    private void connect(int gameID, Session session, String username) throws IOException {
        connections.add(gameID, session);
        try {
            if (gameDao.getGame(gameID) == null) {
                throw new ServerResponseException("Error: invalid game id");
            }
            String color = null;
            if (gameDao.getGame(gameID).whiteUsername() != null && gameDao.getGame(gameID).whiteUsername().equals(username)) {
                color = "white";
            }
            else if (gameDao.getGame(gameID).blackUsername() != null && gameDao.getGame(gameID).blackUsername().equals(username)){
                color = "black";
            }
            else {
                color = "observer";
            }
            var message = String.format("%s has joined the game as %s.", username, color);
            var notification = new NotificationMessage(message);
            connections.broadcast(gameID, session, notification);
            connections.sendToSession(gameID, session, new LoadGameMessage(gameDao.getGame(gameID)));

        }
        catch (ServerResponseException e) {
            connections.sendToSession(gameID, session, new ErrorMessage("Error loading game associated with " + gameID));
        }


    }

    private void move(int gameID, Session session, String username, String cmd) throws IOException{
        try {
            GameData gameData = gameDao.getGame(gameID);
            ChessGame.TeamColor team;
            ChessGame.TeamColor other_team;
            if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
                team = BLACK;
                other_team = WHITE;
            } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
                team = WHITE;
                other_team = BLACK;
            } else {
                throw new ServerResponseException("Observer cannot move!!");
            }

            if (gameData.game() == null) {
                throw new ServerResponseException("Error: chess game has not been created");
            }
            if (!connections.checkInPlay(gameID)) {
                throw new ServerResponseException(" Game is over.");
            }


            MakeMoveCommand move = new Gson().fromJson(cmd, MakeMoveCommand.class);
            ChessGame game = gameData.game();
            try {
                if (!game.teamValidMoves(team).contains(move.getMove())) {
                    throw new InvalidMoveException("Invalid move.");
                }
                game.makeMove(move.getMove());
                GameData newGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                gameDao.updateGame(gameID, game);

                connections.broadcast(gameID, null, new LoadGameMessage(newGame));



                connections.broadcast(gameID, session, new NotificationMessage(username + " made a move " + move.getMoveString()));

                if (game.isInCheckmate(other_team)) {
                    String checkmate_username = "";
                    if (gameData.whiteUsername().equals(username)) {
                        checkmate_username = gameData.blackUsername();
                    }
                    else {
                        checkmate_username = gameData.whiteUsername();
                    }
                    connections.broadcast(gameID, null, new NotificationMessage(checkmate_username + " is in check" +
                            "mate! " + username + " won."));
                    connections.removeAll(gameID);
                }
                if (game.isInCheck(other_team)) {

                    String check_username = "";
                    if (gameData.whiteUsername().equals(username)) {
                        check_username = gameData.blackUsername();
                    }
                    else {
                        check_username = gameData.whiteUsername();
                    }
                    connections.broadcast(gameID, null, new NotificationMessage(check_username + " is in check!"));
                }
            } catch (InvalidMoveException e) {
                connections.sendToSession(gameID, session, new ErrorMessage("Error: " + e.getMessage()));
            }

        }
        catch (ServerResponseException e) {
            connections.sendToSession(gameID, session, new ErrorMessage("Error making move." + e.getMessage()));
        }
    }

    private void leave(int gameID, Session session, String username) throws IOException {
        try {
            GameData gameData = gameDao.getGame(gameID);
            if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
                gameDao.updateGame(gameData, "WHITE", null);
            }
            else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
                gameDao.updateGame(gameData, "BLACK", null);
            }
            connections.broadcast(gameID, session, new NotificationMessage(username + " has left the game."));

            connections.remove(gameID, session);
        }
        catch (ServerResponseException e) {
            connections.sendToSession(gameID, session, new ErrorMessage("Error leaving game. " + e.getMessage()));
        }
    }

    private void resign(int gameID, Session session, String username) throws IOException{
        try {
            GameData game = gameDao.getGame(gameID);
            if ( !(username.equals(game.blackUsername()) || username.equals(game.whiteUsername()))) {
                throw new ServerResponseException("Only players can resign.");
            }
            if (!connections.checkInPlay(gameID)) {
                throw new ServerResponseException("Game is already over!");
            }

            connections.broadcast(gameID, session, new NotificationMessage(username + " has resigned!"));
            connections.sendToSession(gameID, session, new NotificationMessage("You have resigned."));
            connections.removeAll(gameID);
        }
        catch (ServerResponseException e) {
            connections.sendToSession(gameID, session, new ErrorMessage("Error: " + e.getMessage()));
        }


    }
}