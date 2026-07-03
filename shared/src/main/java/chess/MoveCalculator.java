package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculator {

    protected final ChessPiece piece;
    protected final ChessPosition pos;
    protected final int row;
    protected final int col;
    protected final ChessBoard board;

    public MoveCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        this.piece = piece;
        this.pos = pos;
        this.row = pos.getRow();
        this.col = pos.getColumn();
        this.board = board;
    }

    public Collection<ChessMove> moveDiagonalUnlimited() {

        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition current;
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row + i, col + i);
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
        //neg neg diagonal
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row - i, col - i);
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

        //neg pos diagonal
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row - i, col + i);
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

        //pos neg diagonal
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

    public Collection<ChessMove> moveStraightUnlimited() {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition current;

        //up straight
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row+i, col);
            if (row+i>8) {
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

        //down straight
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row-i, col);
            if (row-i<=0) {
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

        //right straight
        for (int i=1; i<8; i++) {
            current = new ChessPosition(row, col+i);
            if (col+i>8) {
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

        //left straight
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


    public boolean inRange(int r, int c) {
        return r>0 && r<9 && c>0 && c<9;
    }

    public boolean emptyOrCaptureable(ChessBoard b, ChessGame.TeamColor c, ChessPosition p){
       return b.getPiece(p)==null||b.getPiece(p).getTeamColor()!=c;
    }

    public boolean captureable(ChessPiece p, ChessGame.TeamColor c) {
        return p != null && p.getTeamColor() != c;
    }

    public Collection<ChessMove> addApplicable(Collection<ChessPosition> positions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPosition current : positions) {
            if (inRange(current.getRow(),current.getColumn()) && emptyOrCaptureable(board, piece.getTeamColor(),current)){
                moves.add(new ChessMove(pos, current, null));
            }

        }

        return moves;
    }

}
