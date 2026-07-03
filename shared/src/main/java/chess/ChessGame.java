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
public class ChessGame implements Cloneable{

    private ChessBoard chessboard;
    private TeamColor turn;

    public ChessGame() {
        turn = TeamColor.WHITE;
        chessboard = new ChessBoard();
        chessboard.resetBoard();
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
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
        ChessPiece p = chessboard.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = p.pieceMoves(chessboard, startPosition);
        Collection<ChessMove> allValidMoves = new ArrayList<>();


        //debug
        System.out.println("all potential moves: " + potentialMoves);
        System.out.println("Current king position: " + getKingPosition(p.getTeamColor()));



        if (p == null) {
            return null;
        }
        else {
            ChessGame gameclone = clone();
            for (ChessMove move : potentialMoves) {

                //add to valid moves if it doesn't leave own king in check
                gameclone.makeMoveWithoutChecking(move, p, gameclone.getBoard());
                if (!gameclone.isInCheck(p.getTeamColor())) {
                    allValidMoves.add(move);
                }
            }
        }

        System.out.println(allValidMoves);
        return allValidMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece p = chessboard.getPiece(move.getStartPosition());
        if (p != null && validMoves(move.getStartPosition()).contains(move) && p.getTeamColor() == turn) {
            if (move.getPromotionPiece() != null) {
                p = new ChessPiece(p.getTeamColor(), move.getPromotionPiece());
            }
            makeMoveWithoutChecking(move, p, chessboard);
            advanceTeamTurn();
        }
        else {
            throw new InvalidMoveException("Not valid!");
        }
    }

    /**
     * Switches whose turn it is
     */
    public void advanceTeamTurn() {
        if (turn == TeamColor.BLACK) {
            turn = TeamColor.WHITE;
        }
        else {
            turn = TeamColor.BLACK;
        }
    }

    //the fake board calls this one so checking doesn't trap it in a loop. the real board calls this one after checking validity.
    public void makeMoveWithoutChecking(ChessMove move, ChessPiece p, ChessBoard board) {
        board.addPiece(move.getEndPosition(), p);
        board.addPiece(move.getStartPosition(), null);
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
                        if (move.getEndPosition().equals(kingPos)) {
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
        if (isInCheck(teamColor) && teamValidMoves(teamColor).size()==0){
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> total_valid_moves = teamValidMoves(teamColor);
        if (total_valid_moves.size()==0 && !isInCheck(teamColor)) {
            return true;
        }
        return false;
    }

    /**
    returns all the valid moves a team could make by any piece - used in checkmate and stalemate functions
     */
    public Collection<ChessMove> teamValidMoves(TeamColor team) {
        ChessPosition currentPos;
        Collection<ChessMove> total_valid_moves = new ArrayList<>();
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                currentPos = new ChessPosition(i,j);
                if (chessboard.getPiece(currentPos)!=null && chessboard.getPiece(currentPos).getTeamColor()==team) {
                    Collection<ChessMove> valid = validMoves(currentPos);
                    for (ChessMove m : valid) {
                        total_valid_moves.add(m);
                    }
                }
            }
        }
        return total_valid_moves;
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
        return Objects.equals(chessboard, chessGame.chessboard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessboard, turn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "chessboard=" + chessboard +
                ", turn=" + turn +
                '}';
    }

    @Override
    public ChessGame clone() {
        try {
            ChessGame clone = (ChessGame) super.clone();
            clone.setTeamTurn(turn);
            ChessBoard newBoard = new ChessBoard();
            //updates the board with new pieces according to what is on the old board
            ChessPiece oldPiece;
            ChessPosition currentPos;
            for (int i=1; i<9; i++) {
                for (int j=1; j<9; j++) {
                    currentPos = new ChessPosition(i,j);
                    oldPiece = chessboard.getPiece(currentPos);
                    if (oldPiece==null) {
                        newBoard.addPiece(currentPos, null);
                    }
                    else {
                        newBoard.addPiece(currentPos, new ChessPiece(oldPiece.getTeamColor(), oldPiece.getPieceType()));
                    }
                }
            }
            clone.setBoard(newBoard);
            return clone;
        }
        catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }


}
