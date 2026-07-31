package websocket.commands;
import static websocket.commands.UserGameCommand.CommandType.LEAVE;



public class LeaveGameCommand extends UserGameCommand{

    public LeaveGameCommand(String auth, int id) {
        super(LEAVE, auth, id);
    }

}
