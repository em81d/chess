package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserDAOMemory implements UserDAO {

    //stores users, then there will be one that stores games, one that stores authTokens, etc
    private ArrayList<UserData> users;


    public UserDAOMemory() {
        users = new ArrayList<>();
    }

    @Override
    public void createUser(UserData u) {
        users.add(u);
        //System.out.println("printing users from UserDAOMemory class");
        //System.out.println(users);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : users) {
            if (user.username().equals(username)) {
                System.out.println("user exists. UserDAOMemory 27");
                return user;
            }
        }
        throw new DataAccessException("username is not associated with a user");
    }

    @Override
    public void clearUsers() {
        users.removeAll(users);
    }

}
