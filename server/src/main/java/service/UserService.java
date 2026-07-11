package service;
import dataaccess.*;
import model.UserData;
import service.RR.*;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest)  { //throws AlreadyTakenException {
        UserDAO userDao = new UserDAOMemory();
        AuthDAO authDao = new AuthDAOMemory();
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        //right now the try works if the username can be found, so the catch is what is supposed to happen - kinda funky?
        try {
            userDao.getUser(username);
//            throw new AlreadyTakenException("that username already exists!");
            return new RegisterResult(username, null, 403);
        }
        catch (Exception e) {
            userDao.createUser(new UserData(username, password, email));
            String authToken = authDao.createAuth(username);
            return new RegisterResult(username, authToken, 200);
        }

    }
    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("u", "a", 200);
    }
    public void logout(LogoutRequest logoutRequest) {}
}

