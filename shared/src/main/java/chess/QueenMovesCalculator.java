package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends MoveCalculator {


    public QueenMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece, pos, board);
    }

    public Collection<ChessMove> calculateQueen() {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(moveDiagonalUnlimited());
        moves.addAll(moveStraightUnlimited());

        return moves;
    }
}
