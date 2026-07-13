package service;
import dataaccess.*;
import model.*;
import service.RR.*;

public class UserService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException { //throws AlreadyTakenException {

        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (userDao.getUser(username) != null) {
            throw new DataAccessException("username already exists");
            //403 already taken/forbidden
        }
        if (password == null || email == null) {
            throw new DataAccessException("password or email blank");
            //400 bad request
        }

        userDao.createUser(new UserData(username, password, email));
        String authToken = authDao.createAuth(username);
        return new RegisterResult(username, authToken, 200);

    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || password == null) {
            //400 bad request
            throw new DataAccessException("username and password are required fields.");
        }

        UserData user = userDao.getUser(username);

        //credentials are correct
        if (user != null && password.equals(user.password())) {
            return new LoginResult(username, authDao.createAuth(username), 200);
        }
        else {
            throw new DataAccessException("wrong username or password.");
            //401 unauthorized
        }

    }


    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        //shouldn't be a problem if authToken is null? it will return 401 either way?

        if (authDao.getAuth(authToken) == null) {
            throw new DataAccessException("no auth token");
            //401 unauthorized
        }

        authDao.deleteAuth(authToken);
        return new LogoutResult(200);
    }
}

