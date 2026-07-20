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

                System.out.println("id being returned: " + ID);
                return ID;
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to create game in database:" + e.getMessage());
        }
    }

    @Override
    public GameData updateGame(int gameID, ChessGame g) throws ServerResponseException{

        String whiteusername;
        String blackusername;
        String gameName;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement1 = conn.prepareStatement("UPDATE game SET json=? WHERE id=?")) {
                preparedStatement1.setInt(2, gameID);
                preparedStatement1.setString(1, new Gson().toJson(g));

                preparedStatement1.executeUpdate();
            }
            try (var preparedStatement2 = conn.prepareStatement("SELECT whiteusername, blackusername, name FROM game WHERE id=?")) {
                preparedStatement2.setInt(1, gameID);
                try (var stmt = preparedStatement2.executeQuery()) {
                    gameName = stmt.getString("name");
                    whiteusername = stmt.getString("whiteusername");
                    blackusername = stmt.getString("blackusername");
                    ChessGame game = new Gson().fromJson(stmt.getString("json"), ChessGame.class);

                    return new GameData(gameID, whiteusername, blackusername, gameName, game);
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to get game from database:" + e.getMessage());
        }

    }

    @Override
    public GameData updateGame(GameData game, String color, String username) throws ServerResponseException {
        String whiteusername = null;
        String blackusername = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteusername=?, blackusername=? WHERE id=?")) {
                preparedStatement.setInt(3, game.gameID());

                if (color.equals("WHITE")) {
                    whiteusername = username;
                }
                else {
                    blackusername = username;
                }

                preparedStatement.setString(1, whiteusername);
                preparedStatement.setString(2, blackusername);

                preparedStatement.executeUpdate();
                return new GameData(game.gameID(), whiteusername, blackusername, game.gameName(), game.game());
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to get game from database:" + e.getMessage());
        }

    }

    @Override
    public GameData getGame(int gameID) throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT name, whiteusername, blackusername, json FROM game WHERE id=?")) {
                preparedStatement.setInt(1, gameID);

                try (var stmt = preparedStatement.executeQuery()) {
                    if (stmt.next()) {
                        String name = stmt.getString("name");
                        String whiteusername = stmt.getString("whiteusername");
                        String blackusername = stmt.getString("blackusername");
                        ChessGame game = new Gson().fromJson(stmt.getString("json"), ChessGame.class);

                        return new GameData(gameID, whiteusername, blackusername, name, game);
                    }
                    else {
                        return null;
                    }
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
    public void clearGames() throws ServerResponseException {

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE game")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to clear games from database:" + e.getMessage());
        }

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
