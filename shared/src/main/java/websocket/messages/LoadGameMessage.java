package websocket.messages;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;


public class LoadGameMessage extends ServerMessage{

    //game variable of type "any"

    public LoadGameMessage() {
        super(LOAD_GAME);
    }
}
