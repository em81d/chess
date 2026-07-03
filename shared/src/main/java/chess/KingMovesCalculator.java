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

        List<ChessPosition> positions = new ArrayList<>(List.of(new ChessPosition(row+1, col)));
        positions.add(new ChessPosition(row-1, col));
        positions.add(new ChessPosition(row, col+1));
        positions.add(new ChessPosition(row, col-1));
        positions.add(new ChessPosition(row+1, col+1));
        positions.add(new ChessPosition(row-1, col-1));
        positions.add(new ChessPosition(row+1, col-1));
        positions.add(new ChessPosition(row-1, col+1));


        return addApplicable(positions);
    }
}
