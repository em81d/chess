package dataaccess;

import exceptions.ServerResponseException;
import model.AuthData;

public interface AuthDAO {

//    createAuth: Create a new authorization.
//    getAuth: Retrieve an authorization given an authToken.
//    deleteAuth: Delete an authorization so that it is no longer valid.

    String createAuth(String username) throws ServerResponseException;
    AuthData getAuth(String authToken) throws ServerResponseException;
    boolean deleteAuth(String authToken) throws ServerResponseException;
    void clearAuths() throws ServerResponseException;

}
