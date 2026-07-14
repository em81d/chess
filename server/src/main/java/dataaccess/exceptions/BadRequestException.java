package dataaccess.exceptions;

public class BadRequestException extends DataAccessException {
    public BadRequestException(String message) {
        super(400, message);
    }
    //get a 400
}
