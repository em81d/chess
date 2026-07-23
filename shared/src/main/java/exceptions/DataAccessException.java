package exceptions;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {

    private final int code;
    private final String message;

    public DataAccessException(String message) {
        super(message);
        code = 400;
        this.message = message;
    }

    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
        code = 400;
        this.message = message;
    }

    public DataAccessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", String.format("Error: %s", message), "success", false));

    }

    public int getCode() {
        return code;
    }

}
