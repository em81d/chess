package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Objects;

public class UserDAOMemory implements UserDAO {

    //stores users, then there will be one that stores games, one that stores authTokens, etc
    private ArrayList<UserData> users;


    public UserDAOMemory() {
        users = new ArrayList<>();
    }

    @Override
    public void createUser(UserData u) {
        users.add(new UserData(u.username(), hashUserPassword(u.password()), u.email()));
        //System.out.println("printing users from UserDAOMemory class");
        //System.out.println(users);
    }

    @Override
    public UserData getUser(String username){
        for (UserData user : users) {
            if (user.username().equals(username)) {
                System.out.println("user exists. UserDAOMemory 27");
                return user;
            }
        }
        return null;
    }

    private String hashUserPassword(String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        return hashedPassword;
    }

    @Override
    public void clearUsers() {
        users.removeAll(users);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDAOMemory that = (UserDAOMemory) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }

    @Override
    public String toString() {
        return "UserDAOMemory{" +
                "users=" + users +
                '}';
    }
}
