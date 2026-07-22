package dataaccess;

import exceptions.DataAccessException;
import exceptions.ServerResponseException;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlDAO {


    protected void configureDatabase(String createStatement)  throws ServerResponseException {
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
