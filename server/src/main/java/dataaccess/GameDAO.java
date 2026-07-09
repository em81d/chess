package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    /*
            createGame: Create a new game.
            getGame: Retrieve a specified game with the given game ID.
            listGames: Retrieve all games.
            updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.


            where does clear DAO go??
    */

    void createGame(GameData d);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    GameData updateGame(int gameID, ChessGame game);
}
