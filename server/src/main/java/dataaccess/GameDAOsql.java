package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ServerResponseException;
import model.AbbreviatedGame;
import model.GameData;

import javax.xml.crypto.Data;
import java.rmi.ServerError;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class GameDAOsql implements GameDAO {


    public GameDAOsql() {
        System.out.println("trying to start server...");
        try {
            configureDatabase();
        }
        catch (ServerResponseException e) {
            System.out.println("failed to start server. Message: " + e.getMessage());
        }
    }


    @Override
    public int createGame(String name)  throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (name, json) VALUES(?, ?)", RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, new Gson().toJson(new ChessGame()));

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }

                return ID;
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to create game in database:" + e.getMessage());
        }
    }

    @Override
    public GameData updateGame(int gameID, ChessGame g){


        //temporary method
        return new GameData(0, "w", "b", "game", null);
    }

    @Override
    public GameData updateGame(GameData game, String color, String username) {


        //temporary method
        return new GameData(0, "w", "b", "game", null);
    }

    @Override
    public GameData getGame(int gameID) throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT name, whiteusername, blackusername, json FROM game WHERE id=?")) {
                preparedStatement.setInt(1, gameID);
                try (var stmt = preparedStatement.executeQuery()) {
                    String name = stmt.getString("name");
                    String whiteusername = stmt.getString("whiteusername");
                    String blackusername = stmt.getString("blackusername");
                    ChessGame game = new Gson().fromJson(stmt.getString("json"), ChessGame.class);

                    return new GameData(gameID, whiteusername, blackusername, name, game);
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to get game from database:" + e.getMessage());
        }

    }

    @Override
    public Collection<AbbreviatedGame> listGames() throws ServerResponseException {
        Collection<AbbreviatedGame> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT id, name, whiteusername, blackusername, json FROM game")) {
                try (var stmt = preparedStatement.executeQuery()) {
                    while (stmt.next()){
                        String name = stmt.getString("name");
                        String whiteusername = stmt.getString("whiteusername");
                        String blackusername = stmt.getString("blackusername");
                        int id = stmt.getInt("id");

                        result.add(new AbbreviatedGame(id, whiteusername, blackusername, name));
                    }
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to list games from database:" + e.getMessage());
        }

        return result;
    }

    @Override
    public void clearGames() {

    }


    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS  game (
              id int NOT NULL AUTO_INCREMENT,
              name varchar(100) NOT NULL,
              whiteusername varchar(100),
              blackusername varchar(100),
              json TEXT DEFAULT NULL,
              PRIMARY KEY (id)
            );
            """
    ;


    private void configureDatabase()  throws ServerResponseException {
        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e) {
            throw new ServerResponseException("Error: unable to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to configure database: " + e.getMessage());
        }
    }

}
