package websocket.messages;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;


public class ErrorMessage extends ServerMessage{

    private final String errorMessage;

    public ErrorMessage(String message) {
        super(ERROR);
        errorMessage = message;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

}
