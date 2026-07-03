package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class PawnMovesCalculator extends MoveCalculator{


    public PawnMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece, pos, board);
    }

    public Collection<ChessMove> calculatePawn() {
        Collection<ChessMove> moves = new ArrayList<>();
        List<ChessPosition> positions = new ArrayList<>();

        if (piece.getTeamColor() == WHITE) {

            //straight moves white (not diagonal)
            positions.add(new ChessPosition(row + 1, col));
            if (row == 2) {
                positions.add(new ChessPosition(row + 2, col));
            }

            for (ChessPosition current : positions) {
                if (inRange(current.getRow(), current.getColumn()) && (board.getPiece(current) == null)) {
                    moves.addAll(addPawnMove(current));
                }
            }

            //diagonal moves white (capture)
            positions = new ArrayList<>();
            positions.add(new ChessPosition(row + 1, col + 1));
            positions.add(new ChessPosition(row + 1, col - 1));

            addDiagonalCaptures(positions, moves);
        }

        else {
            //straight moves black (not diagonal)
            positions.add(new ChessPosition(row - 1, col));
            if (row == 7) {
                //if it can advance by two, make sure it is free to advance by 1 first (can't jump)
                if (board.getPiece(new ChessPosition(row-1, col)) == null) {
                    positions.add(new ChessPosition(row - 2, col));
                }
            }

            for (ChessPosition current : positions) {
                if (inRange(current.getRow(), current.getColumn()) && (board.getPiece(current) == null)) {
                    moves.addAll(addPawnMove(current));
                }
            }

            //diagonal moves black (capture)
            positions = new ArrayList<>();
            positions.add(new ChessPosition(row - 1, col + 1));
            positions.add(new ChessPosition(row - 1, col - 1));

            addDiagonalCaptures(positions, moves);
        }


        return moves;
    }


    public Collection<ChessMove> addPawnMove(ChessPosition end){
        Collection<ChessMove> moves = new ArrayList<>();
        if ((piece.getTeamColor() == WHITE && row == 7) || (piece.getTeamColor() == BLACK && row == 2)) {
            moves.add(new ChessMove(pos, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(pos, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(pos, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(pos, end, ChessPiece.PieceType.KNIGHT));
        }
        else {
            moves.add(new ChessMove(pos, end, null));
        }
        return moves;
    }

    public void addDiagonalCaptures(List<ChessPosition> p, Collection<ChessMove> m) {
        for (ChessPosition current : p) {
            if (inRange(current.getRow(),current.getColumn()) && captureable(board.getPiece(current), piece.getTeamColor())){
                m.addAll(addPawnMove(current));
            }
        }
    }
}
