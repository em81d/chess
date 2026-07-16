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


public class GameDAOsql implements GameDAO {


    public GameDAOsql() throws ServerResponseException {
        configureDatabase();
    }


    @Override
    public int createGame(String name) {

    }

    @Override
    public GameData updateGame(int gameID, ChessGame g){

    }

    @Override
    public GameData updateGame(GameData game, String color, String username) {

    }

    @Override
    public GameData getGame(int gameID){

    }

    @Override
    public Collection<AbbreviatedGame> listGames() {

    }

    @Override
    public void clearGames() {

    }

    public int newGameId() {

    }


    private void configureDatabase()  throws ServerResponseException {
        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e) {
            throw new ServerResponseException("Error: unable to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {

        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to configure database:" + e.getMessage());
        }
    }

}
