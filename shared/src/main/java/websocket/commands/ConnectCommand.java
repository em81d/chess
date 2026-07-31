package websocket.commands;
import static websocket.commands.UserGameCommand.CommandType.CONNECT;



public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(String auth, int id) {
        super(CONNECT, auth, id);
    }
}
