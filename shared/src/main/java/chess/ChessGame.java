package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard chessboard;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> potentialMoves = chessboard.getPiece(startPosition).pieceMoves(chessboard, startPosition);
        Collection<ChessMove> allValidMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves) {
            //add to valid moves if doesn't leave own king in check
        }

        return allValidMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = getKingPosition(teamColor);

        ChessPosition currentPos;
        ChessPiece currentPiece;
        Collection<ChessMove> currentMoves;

        //loops through every square
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                currentPos = new ChessPosition(i,j);
                currentPiece = chessboard.getPiece(currentPos);
                //checks if the current piece is opponent's color, if so, if any of their potential moves get them to king's position
                if (currentPiece!=null && currentPiece.getTeamColor()!=teamColor) {
                    currentMoves = currentPiece.pieceMoves(chessboard, currentPos);
                    for (ChessMove move : currentMoves) {
                        if (move.getEndPosition() == kingPos) {
                            return true;
                        }
                    }
                }
            }
        }
        //if it hasn't returned true by the end, king is safe
        return false;
    }


    public ChessPosition getKingPosition(TeamColor color) {
        ChessPiece current;
        ChessPosition currentPos;

        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                currentPos = new ChessPosition(i,j);
                current = chessboard.getPiece(currentPos);
                if (current != null && current.getTeamColor()==color && current.getPieceType()== ChessPiece.PieceType.KING) {
                    return currentPos;
                }
            }
        }

        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessboard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessboard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(chessboard, chessGame.chessboard);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chessboard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "chessboard=" + chessboard +
                '}';
    }
}
