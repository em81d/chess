package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.AbbreviatedGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
    public GameData updateGame(GameData game, String color, String username) {
        for (int i=0; i<games.size(); i++) {
            if (games.get(i).gameID() == game.gameID()) {
                if (color.equals("WHITE")) {
                    games.set(i, new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
                } else { // if (color.equals("BLACK")) {
                    games.set(i, new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
                }
                return games.get(i);
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
    public Collection<AbbreviatedGame> listGames() {
        Collection<AbbreviatedGame> abbreviatedGames = new ArrayList<>();
        for (GameData game : games) {
            abbreviatedGames.add(new AbbreviatedGame(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return abbreviatedGames;
    }

    @Override
    public void clearGames() {
        games.removeAll(games);
    }

    public int newGameId() {
        return gameCount + 100;
    }

}
