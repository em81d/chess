package dataaccess;
import chess.ChessGame;
import exceptions.ServerResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    /*
            createGame: Create a new game.
            getGame: Retrieve a specified game with the given game ID.
            listGames: Retrieve all games.
            updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
            //This is used when players join a game or when a move is made.


            where does clear DAO go??
    */

    int createGame(String name) throws ServerResponseException;
    GameData getGame(int gameID) throws ServerResponseException;
    Collection<GameData> listGames() throws ServerResponseException;
    GameData updateGame(int gameID, ChessGame game) throws ServerResponseException;
    GameData updateGame(GameData game, String color, String username) throws ServerResponseException;
    void clearGames() throws ServerResponseException;
}
