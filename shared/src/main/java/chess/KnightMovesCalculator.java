package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator {

    private final ChessPiece piece;
    private final ChessPosition pos;
    private final int row;
    private final int col;
    private final ChessBoard board;

    public KnightMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        this.piece = piece;
        this.pos = pos;
        this.row = pos.getRow();
        this.col = pos.getColumn();
        this.board = board;
    }

    public Collection<ChessMove> calculateKnight() {
        Collection<ChessMove> moves = new ArrayList<>();

        List<ChessPosition> positions = new ArrayList<>(List.of(new ChessPosition(row+2, col+1), new ChessPosition(row+2, col-1), new ChessPosition(row-2, col+1), new ChessPosition(row-2, col-1), new ChessPosition(row+1, col+2), new ChessPosition(row-1, col+2), new ChessPosition(row+1, col-2), new ChessPosition(row-1, col-2)));


        for (ChessPosition current : positions) {

            if (current.getRow()<=8 && current.getColumn()<=8 && current.getRow()>0 && current.getColumn()>0 && (board.getPiece(current) == null || board.getPiece(current).getTeamColor() != piece.getTeamColor())){
                moves.add(new ChessMove(pos, current, null));
            }

        }

        return moves;
    }
}
