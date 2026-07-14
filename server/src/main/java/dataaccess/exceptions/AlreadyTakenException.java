package dataaccess.exceptions;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException(String message) {
        super(403, message);
    }
    //thrown to get a 403 forbidden response
}
