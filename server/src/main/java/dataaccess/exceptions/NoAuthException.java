package dataaccess.exceptions;

public class NoAuthException extends DataAccessException {
    public NoAuthException(String message) {
        super(401, message);
    }
    //thrown to get a 401 unauthorized response
}
