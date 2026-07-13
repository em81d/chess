package dataaccess;
import model.AuthData;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class AuthDAOMemory implements AuthDAO {

    private Collection<AuthData> auths;

    public AuthDAOMemory () {
        auths = new ArrayList<>();
    }

    @Override
    public String createAuth(String username) {
        String auth = generateToken();
        auths.add(new AuthData(auth, username));
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken){
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }


    @Override
    public void deleteAuth(String authToken){
        AuthData toDelete = null;
        for (AuthData auth  : auths) {
            if (auth.authToken().equals(authToken)) {
                toDelete = auth;
            }
        }
    }



    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clearAuths() {
        auths.removeAll(auths);
    }

}
