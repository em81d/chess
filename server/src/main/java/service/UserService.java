package service;

import service.RR.*;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest) {
        return new RegisterResult("myUsername", "myAuth", 200);
    }
    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("myUsername", "myAuth", 200);
    }
    public void logout(LogoutRequest logoutRequest) {}
}
