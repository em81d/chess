package websocket.commands;

import chess.ChessMove;

import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getMoveString() {
        int start_row = move.getStartPosition().getRow();
        String start_col ="";
        switch (move.getStartPosition().getColumn()) {
            case 1 -> start_col = "a";
            case 2 -> start_col = "b";
            case 3 -> start_col = "c";
            case 4 -> start_col = "d";
            case 5 -> start_col = "e";
            case 6 -> start_col = "f";
            case 7 -> start_col = "g";
            case 8 -> start_col = "h";
        }

        int end_row = move.getEndPosition().getRow();
        String end_col ="";
        switch (move.getEndPosition().getColumn()) {
            case 1 -> end_col = "a";
            case 2 -> end_col = "b";
            case 3 -> end_col = "c";
            case 4 -> end_col = "d";
            case 5 -> end_col = "e";
            case 6 -> end_col = "f";
            case 7 -> end_col = "g";
            case 8 -> end_col = "h";
        }
        return "from " + start_col + start_row + " to " + end_col + end_row + ". ";
    }

}
