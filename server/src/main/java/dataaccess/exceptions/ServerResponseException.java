package dataaccess.exceptions;

public class ServerResponseException extends DataAccessException {
    public ServerResponseException(String message) {
        super(500, message);
    }
}
