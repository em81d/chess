package dataaccess;
import model.AuthData;

import java.util.Objects;
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
    public boolean deleteAuth(String authToken){
        AuthData toDelete = null;
        for (AuthData auth  : auths) {
            if (auth.authToken().equals(authToken)) {
                toDelete = auth;
            }
        }

        if (toDelete != null) {
            auths.remove(toDelete);
            return true;
        }
        else {
            return false;
        }
    }



    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clearAuths() {
        auths.removeAll(auths);
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthDAOMemory that = (AuthDAOMemory) o;
        return Objects.equals(auths, that.auths);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(auths);
    }

    @Override
    public String toString() {
        return "AuthDAOMemory{" +
                "auths=" + auths +
                '}';
    }
}
