package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends MoveCalculator{


    public BishopMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece,pos,board);
    }

    public Collection<ChessMove> calculateBishop() {

        return moveDiagonalUnlimited();
    }
}
