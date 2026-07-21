package dataaccess;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ServerResponseException;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class AuthDAOsql extends SqlDAO implements AuthDAO {


    public AuthDAOsql () {

        System.out.println("trying to start server...");
        try {
            configureDatabase(createStatement);
        }
        catch (ServerResponseException e) {
            System.out.println("failed to start server. Message: " + e.getMessage());
        }
    }


    @Override
    public String createAuth(String username) throws ServerResponseException {

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (username, authtoken) VALUES(?, ?)")) {
                preparedStatement.setString(1, username);
                String authToken = generateToken();
                preparedStatement.setString(2, authToken);

                preparedStatement.executeUpdate();

                return authToken;

            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to create user in database:" + e.getMessage());
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws ServerResponseException {

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM auth WHERE authtoken=?")) {
                preparedStatement.setString(1, authToken);
                try (var stmt = preparedStatement.executeQuery()) {
                    if (stmt.next()) {
                        return new AuthData(authToken, stmt.getString("username"));
                    }
                    else {
                        return null;
                    }

                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Unable to get auth from database:" + e.getMessage());
        }

    }


    @Override
    public boolean deleteAuth(String authToken) throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authtoken=?")) {
                preparedStatement.setString(1, authToken);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    return false;
                }
                return true;
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to delete auth data from database:" + e.getMessage());
        }

    }



    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clearAuths() throws ServerResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE auth")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ServerResponseException("Error: Unable to clear auth data from database:" + e.getMessage());
        }
    }


    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS  auth (
              authtoken varchar(100) NOT NULL,
              username varchar(100) NOT NULL,
              PRIMARY KEY (authtoken)
            );
            """
            ;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthDAOsql that = (AuthDAOsql) o;
        return Objects.equals(createStatement, that.createStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(createStatement);
    }

    @Override
    public String toString() {
        return "AuthDAOsql{" +
                "createStatement='" + createStatement + '\'' +
                '}';
    }
}
