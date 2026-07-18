package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ServerResponseException;
import model.UserData;

public interface UserDAO {

    /*
    * createUser: Create a new user.
      getUser: Retrieve a user with the given username.
            * */

    void createUser(UserData u) throws ServerResponseException;
    UserData getUser(String username) throws ServerResponseException;
    void clearUsers() throws ServerResponseException;
}
