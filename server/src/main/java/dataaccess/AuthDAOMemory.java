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
    public AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData auth : auths) {
            if (auth.authToken() == authToken) {
                return auth;
            }
        }
        throw new DataAccessException("auth token does not exist");
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        AuthData toDelete = null;
        for (AuthData auth  : auths) {
            if (auth.authToken() == authToken) {
                toDelete = auth;
            }
        }


        if (toDelete == null) { throw new DataAccessException("tried to delete nonexisting auth"); } else { auths.remove(toDelete); }

    }



    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clearAuths() {
        auths.removeAll(auths);
    }

}
