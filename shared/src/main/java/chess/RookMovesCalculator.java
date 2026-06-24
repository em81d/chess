package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {

    private final ChessPiece piece;
    private final ChessPosition pos;
    private final int row;
    private final int col;
    private final ChessBoard board;

    public RookMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        this.piece = piece;
        this.pos = pos;
        this.row = pos.getRow();
        this.col = pos.getColumn();
        this.board = board;
    }

    public Collection<ChessMove> calculateRook() {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition current;
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row + i, col);
            if (row+i>8) {
                break;
            }
            else if (board.getPiece(current) == null){
                moves.add(new ChessMove(pos, current, null));
            }
            else if (board.getPiece(current).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(pos, current, null));
                break;
            }
            else if (board.getPiece(current).getTeamColor() == piece.getTeamColor()) {
                break;
            }
        }
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row - i, col);
            if (row - i <= 0) {
                break;
            } else if (board.getPiece(current) == null) {
                moves.add(new ChessMove(pos, current, null));
            } else if (board.getPiece(current).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(pos, current, null));
                break;
            }
            else if (board.getPiece(current).getTeamColor() == piece.getTeamColor()) {
                break;
            }
        }

        for (int i=1; i<8; i++) {
            current = new ChessPosition(row, col + i);
            if (col+i>8) {
                break;
            }
            else if (board.getPiece(current) == null ) {
                moves.add(new ChessMove(pos, current, null));
            }
            else if (board.getPiece(current).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(pos, current, null));
                break;
            }
            else if (board.getPiece(current).getTeamColor() == piece.getTeamColor()) {
                break;
            }
        }

        for (int i=1; i<8; i++) {
            current = new ChessPosition(row, col-i);
            if (col-i<=0) {
                break;
            }
            else if (board.getPiece(current)==null) {
                moves.add(new ChessMove(pos, current, null));
            }
            else if (board.getPiece(current).getTeamColor()!=piece.getTeamColor()) {
                moves.add(new ChessMove(pos, current, null));
                break;
            }
            else if (board.getPiece(current).getTeamColor() == piece.getTeamColor()) {
                break;
            }

        }

        return moves;
    }
}
