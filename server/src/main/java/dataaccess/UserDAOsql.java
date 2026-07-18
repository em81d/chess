package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ServerResponseException;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;



public class UserDAOsql implements UserDAO {

    //stores users, then there will be one that stores games, one that stores authTokens, etc
    private ArrayList<UserData> users;


    public UserDAOsql() {
        System.out.println("trying to start server...");
        try {
            configureDatabase();
        }
        catch (ServerResponseException e) {
            System.out.println("failed to start server. Message: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData u) throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, email, password) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, u.username());
                preparedStatement.setString(2, u.email());
                preparedStatement.setString(3, u.password());

                preparedStatement.executeUpdate();

            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to create user in database:" + e.getMessage());
        }

    }

    @Override
    public UserData getUser(String username) throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT password, email FROM user WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var stmt = preparedStatement.executeQuery()) {

                    if (stmt.next()) {
                        return new UserData(username, stmt.getString("password"), stmt.getString("email"));
                    }
                    else {
                        return null;
                    }

                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Unable to get user from database:" + e.getMessage());
        }

    }

    @Override
    public void clearUsers() throws ServerResponseException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE user")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to clear users from database:" + e.getMessage());
        }
    }


    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS  user (
              username varchar(100) NOT NULL,
              email varchar(100) NOT NULL,
              password varchar(100) NOT NULL,
              PRIMARY KEY (username)
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
