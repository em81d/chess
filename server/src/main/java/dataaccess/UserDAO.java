package dataaccess;

import model.UserData;

public interface UserDAO {

    /*
    * createUser: Create a new user.
      getUser: Retrieve a user with the given username.
            * */

    void createUser(UserData u);
    UserData getUser(String username) throws DataAccessException;
    void clearUsers();
}
