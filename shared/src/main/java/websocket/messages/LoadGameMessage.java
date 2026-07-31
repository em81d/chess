package websocket.messages;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class LoadGameMessage extends ServerMessage{

    private final String text;

    //game variable of type "any"

    public LoadGameMessage(String text) {
        super(LOAD_GAME);
        this.text = text;
    }

    public LoadGameMessage() {
        super(LOAD_GAME);
        this.text = "Loading game...";
    }

    public String getText() {
        return text;
    }


}
