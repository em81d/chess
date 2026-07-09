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
    public void createAuth(String username) {
        auths.add(new AuthData(generateToken(), username));
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth : auths) {
            if (auth.authToken() == authToken) {
                return auth;
            }
        }
        return null;
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

}
