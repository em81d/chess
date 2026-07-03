package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends MoveCalculator{


    public RookMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece, pos, board);
    }

    public Collection<ChessMove> calculateRook() {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(moveStraightUnlimited());

        return moves;
    }
}
