package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {

    private final ChessPiece piece;
    private final ChessPosition pos;
    private final int row;
    private final int col;
    private final ChessBoard board;

    public BishopMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        this.piece = piece;
        this.pos = pos;
        this.row = pos.getRow();
        this.col = pos.getColumn();
        this.board = board;
    }

    public Collection<ChessMove> calculateBishop() {
        Collection<ChessMove> moves = new ArrayList<>();
//        System.out.println("POSITION OF PIECE: " + pos);
        ChessPosition current;
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row + i, col + i);
//            System.out.println("EVALUATING POSITION: " + current);
            if (row+i>8 || col+i>8) {
                break;
            }
            else if (board.getPiece(current) == null){
                moves.add(new ChessMove(pos, current, null));
            }
            else if (board.getPiece(current).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(pos, current, null)); //should it already know the piece type?
                break;
            }
            else if (board.getPiece(current).getTeamColor() == piece.getTeamColor()) {
                break;
            }
        }
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row - i, col - i);
//            System.out.println("EVALUATING POSITION: " + current);
            if (row - i <= 0 || col - i <= 0) {
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
            current = new ChessPosition(row - i, col + i);
//            System.out.println("EVALUATING POSITION: " + current);
            if (row-i<=0 || col+i>8) {
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
            current = new ChessPosition(row+i, col-i);
            if (row+i>8 || col-i<=0) {
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
