package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOMemory implements GameDAO {


    private ArrayList<GameData> games;

    public GameDAOMemory() {
        games = new ArrayList<>();
    }


    @Override
    public void createGame(GameData d) {
        games.add(d);
    }

    @Override
    public GameData updateGame(int gameID, ChessGame g) {
        GameData gd = null;
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                gd = game;
            }
        }
        return (gd == null) ? null : new GameData(gd.gameID(), gd.whiteUsername(), gd.blackUsername(), gd.gameName(), g );
    }

    @Override
    public GameData getGame(int gameID) {
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

}
