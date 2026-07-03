package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator extends MoveCalculator{


    public PawnMovesCalculator(ChessPiece piece, ChessPosition pos, ChessBoard board) {
        super(piece, pos, board);
    }

    public Collection<ChessMove> calculatePawn() {
        Collection<ChessMove> moves = new ArrayList<>();
        List<ChessPosition> positions = new ArrayList<>();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            //straight moves white (not diagonal)
            positions.add(new ChessPosition(row + 1, col));
            if (row == 2) {
                positions.add(new ChessPosition(row + 2, col));
            }

            for (ChessPosition current : positions) {
                if (inRange(current.getRow(), current.getColumn()) && (board.getPiece(current) == null)) {
                    if (current.getRow() < 8) {
                        moves.add(new ChessMove(pos, current, null));
                    } else if (current.getRow() == 8) {
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.BISHOP));
                    }
                }
            }

            //diagonal moves white (capture)
            positions = new ArrayList<>();
            positions.add(new ChessPosition(row + 1, col + 1));
            positions.add(new ChessPosition(row + 1, col - 1));
            for (ChessPosition current : positions) {
                if (inRange(current.getRow(),current.getColumn())&&(board.getPiece(current)!= null&&board.getPiece(current).getTeamColor()!= piece.getTeamColor())){
                    if (current.getRow() < 8) {
                        moves.add(new ChessMove(pos, current, null));
                    } else if (current.getRow() == 8) {
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.BISHOP));
                    }
                }
            }
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
                    if (current.getRow() > 1) {
                        moves.add(new ChessMove(pos, current, null));
                    } else if (current.getRow() == 1) {
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.BISHOP));
                    }
                }
            }

            //diagonal moves black (capture)
            positions = new ArrayList<>();
            positions.add(new ChessPosition(row - 1, col + 1));
            positions.add(new ChessPosition(row - 1, col - 1));
            for (ChessPosition current : positions) {
                if(inRange(current.getRow(),current.getColumn())&&(board.getPiece(current)!=null&&board.getPiece(current).getTeamColor()!=piece.getTeamColor())){
                    if (current.getRow() > 1) {
                        moves.add(new ChessMove(pos, current, null));
                    } else if (current.getRow() == 1) {
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(pos, current, ChessPiece.PieceType.BISHOP));
                    }
                }
            }
        }


        return moves;
    }
}
