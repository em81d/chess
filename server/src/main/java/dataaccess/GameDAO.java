package dataaccess;
import chess.ChessGame;
import dataaccess.exceptions.ServerResponseException;
import model.AbbreviatedGame;
import model.GameData;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Map;

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
    Collection<AbbreviatedGame> listGames() throws ServerResponseException;
    GameData updateGame(int gameID, ChessGame game) throws ServerResponseException;
    GameData updateGame(GameData game, String color, String username) throws ServerResponseException;
    void clearGames() throws ServerResponseException;
}
