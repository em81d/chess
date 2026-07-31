package websocket.commands;
import static websocket.commands.UserGameCommand.CommandType.RESIGN;



public class ResignCommand extends UserGameCommand {

    public ResignCommand(String authToken, int gameID) {
        super(RESIGN, authToken, gameID);
    }

}
