package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator extends MoveCalculator{


    public KingMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece, pos, board);
    }

    public Collection<ChessMove> calculateKing() {
        Collection<ChessMove> moves = new ArrayList<>();

        List<ChessPosition> positions = new ArrayList<>(List.of(new ChessPosition(row+1, col), new ChessPosition(row-1, col), new ChessPosition(row, col+1), new ChessPosition(row, col-1)));
        positions.add(new ChessPosition(row+1, col+1));
        positions.add(new ChessPosition(row-1, col-1));
        positions.add(new ChessPosition(row+1, col-1));
        positions.add(new ChessPosition(row-1, col+1));


        for (ChessPosition current : positions) {

            if (inRange(current.getRow(), current.getColumn()) && (board.getPiece(current) == null || board.getPiece(current).getTeamColor() != piece.getTeamColor())){
                moves.add(new ChessMove(pos, current, null));
            }

        }

        return moves;
    }
}
