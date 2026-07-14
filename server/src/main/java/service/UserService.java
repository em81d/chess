package service;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.NoAuthException;
import model.*;
import service.reqRes.*;

import java.util.Objects;

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
            throw new AlreadyTakenException("username already exists");
            //403 already taken/forbidden
        }
        if (password == null || email == null) {
            throw new BadRequestException("password or email blank");
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
            throw new BadRequestException("username and password are required fields.");
        }

        UserData user = userDao.getUser(username);

        //credentials are correct
        if (user != null && password.equals(user.password())) {
            return new LoginResult(username, authDao.createAuth(username), 200);
        }
        else {
            throw new NoAuthException("wrong username or password.");
            //401 unauthorized
        }

    }


    public LogoutResult logout(LogoutRequest logoutRequest) throws NoAuthException {
        String authToken = logoutRequest.authToken();

        if (authDao.getAuth(authToken) == null) {
            throw new NoAuthException("no auth token");
            //401 unauthorized
        }

        authDao.deleteAuth(authToken);
        return new LogoutResult(200);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserService that = (UserService) o;
        return Objects.equals(userDao, that.userDao) && Objects.equals(authDao, that.authDao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDao, authDao);
    }

    @Override
    public String toString() {
        return "UserService{" +
                "userDao=" + userDao +
                ", authDao=" + authDao +
                '}';
    }
}

