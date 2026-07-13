package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOMemory implements GameDAO {


    private ArrayList<GameData> games;
    int gameCount;

    public GameDAOMemory() {
        games = new ArrayList<>();
        gameCount = 0;
    }


    @Override
    public int createGame(String name) {
        int id = newGameId();
        GameData gd = new GameData(id, null, null, name, new ChessGame());
        games.add(gd);
        gameCount++;
        return id;
    }

    @Override
    public GameData updateGame(int gameID, ChessGame g){
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), g );
            }
        }
        return null; //????
    }

    @Override
    public GameData updateGame(int gameID, String color, String username) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                if (color.equals("WHITE")) {
                    game = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game() );
                    return game;
                }
                else if (color.equals("BLACK")) {
                    game = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                    return game;
                }
            }
        }
//        throw new DataAccessException("gameID does not exist");
        return null; //????
    }

    @Override
    public GameData getGame(int gameID){
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return games;
    }

    @Override
    public void clearGames() {
        games.removeAll(games);
    }

    public int newGameId() {
        return gameCount + 100;
    }

}
