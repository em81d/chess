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

    public RegisterResult register(RegisterRequest registerRequest)  { //throws AlreadyTakenException {

        //the problem right now is that it creates a new DAO every time it's called, so it is always empty, so there is never a user with the same username
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        //right now the try works if the username can be found, so the catch is what is supposed to happen - kinda funky?
        try {
            userDao.getUser(username);
//            throw new AlreadyTakenException("that username already exists!");
            return new RegisterResult(username, "", 403);
        }
        catch (DataAccessException e) {
//            System.out.println("printing exception thrown in UserService: " + e);
            userDao.createUser(new UserData(username, password, email));
            String authToken = authDao.createAuth(username);
            return new RegisterResult(username, authToken, 200);
        }

    }
    public LoginResult login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        try {
            UserData user = userDao.getUser(username);
            if (password.equals(user.password())) {
                return new LoginResult(username, authDao.createAuth(username), 200);
            }
            else {
                return new LoginResult(username, "", 401);
            }

        }
        catch (DataAccessException e) {
            return new LoginResult(username, "", 401);
        }
    }


    public LogoutResult logout(LogoutRequest logoutRequest) {
        String authToken = logoutRequest.authToken();
        try {
            authDao.getAuth(authToken);
            authDao.deleteAuth(authToken);
            return new LogoutResult(200);
        }
        catch (DataAccessException e) {
            return new LogoutResult(401);
        }
    }
}

