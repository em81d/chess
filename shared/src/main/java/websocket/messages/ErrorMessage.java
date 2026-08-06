package websocket.messages;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;


public class ErrorMessage extends ServerMessage {

    private final String message;

    public ErrorMessage(String message) {
        super(ERROR);
        this.message = message;
    }


    public String getErrorMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
