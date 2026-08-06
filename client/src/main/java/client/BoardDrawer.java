package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class BoardDrawer {

    public BoardDrawer() {

    }

    public void drawBoard(boolean isWhite, ChessGame game, Collection<ChessPosition> highlighted) {
        ChessBoard board = game.getBoard();
        System.out.print("\u001b[49;38;2;127;161;124;1m");

        if (isWhite) {

            System.out.println("    a   b   c   d   e  f   g   h   \u001b[49m");

            ChessPosition pos;
            for (int i=8; i>0; i--) {
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + " \u001b[30m");
                for (int j=1; j<9; j++) {
                    pos = new ChessPosition(i,j);
                    printSquare(i,j,board,highlighted.contains(pos));
                }
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + "  \u001b[49m\n");
            }
            System.out.print("\u001b[49;38;2;127;161;124;1m");
            System.out.println("    a   b   c   d   e  f   g   h   \u001b[49m");
        }
        else {
            System.out.println("    h   g   f   e   d  c   b   a   \u001b[49m");

            ChessPosition pos;
            for (int i=1; i<9; i++) {
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + " \u001b[30m");
                for (int j=8; j>0; j--) {
                    pos = new ChessPosition(i,j);
                    printSquare(i,j,board,highlighted.contains(pos));
                }
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + "  \u001b[49m\n");
            }
            System.out.print("\u001b[49;38;2;127;161;124;1m");
            System.out.println("    h   g   f   e   d  c   b   a   \u001b[49m");
        }
        System.out.print("\u001b[39m");

    }

    private void printSquare(int i, int j, ChessBoard board, boolean highlighted)  {
        ChessPosition pos = new ChessPosition(i,j);
        if (i % 2 != j % 2) {
            //white square
            if (highlighted) {
                System.out.print("\u001b[48;2;254;255;199m");
            }
            else {
                System.out.print("\u001b[48;2;214;191;206m");
            }
        }
        else {
            //black square
            if (highlighted) {
                System.out.print("\u001b[48;2;195;198;108m");

            } else {
                System.out.print("\u001b[48;2;128;102;119m");

            }
        }
        if (board.getPiece(pos) == null) {
            System.out.print(EMPTY);
        }
        else {
            printPiece(board.getPiece(pos));
        }
    }

    private void printPiece(ChessPiece piece) {
        if (piece.getTeamColor() == WHITE){
            switch (piece.getPieceType()) {
                case ROOK -> {
                    System.out.print(WHITE_ROOK);
                }
                case BISHOP -> {
                    System.out.print(WHITE_BISHOP);
                }
                case QUEEN -> {
                    System.out.print(WHITE_QUEEN);
                }
                case KING -> {
                    System.out.print(WHITE_KING);
                }
                case KNIGHT -> {
                    System.out.print(WHITE_KNIGHT);
                }
                case PAWN -> {
                    System.out.print(WHITE_PAWN);
                }
                default -> {
                    ;
                }
            }
        }
        else {
            switch (piece.getPieceType()) {
                case ROOK -> {
                    System.out.print(BLACK_ROOK);
                }
                case BISHOP -> {
                    System.out.print(BLACK_BISHOP);
                }
                case QUEEN -> {
                    System.out.print(BLACK_QUEEN);
                }
                case KING -> {
                    System.out.print(BLACK_KING);
                }
                case KNIGHT -> {
                    System.out.print(BLACK_KNIGHT);
                }
                case PAWN -> {
                    System.out.print(BLACK_PAWN);
                }
                default -> {
                    ;
                }
            }
        }
    }


    public void printPostloginHelp() {
        System.out.println("\nWelcome to your account homepage! Start by hitting -l to see what games have already\n been created," +
                " and then if you'd like you can join one to play with -p! When you join a game, you \nwill need the game id that " +
                "is printed during the list dialog. If you aren't feeling ready to \njoin a game just yet, you can also pick a game id " +
                "and join as an observer with -o. If there isn't \nalready a game you want to join, create one with -c. When you're " +
                "all done playing, it's a simple -e \nto exit and log out. Happy chess playing!\n\t\t\t\t\t\t*****\t\t\t");
    }

    public void printPreloginHelp() {
        System.out.println("To use the chess application, start by logging in or creating an account!");
        System.out.println("Type -l and hit enter to log in, or type -r and hit enter to create an account.");
        System.out.println("Once you've entered a command, the terminal will walk you through the next steps.");
        System.out.println("Then you will get to the next part of the application, where you will be able to play chess!");
        System.out.println("\n\t\t\t\t\t\t*\t*\t*\t\t\t\n");
    }

    public void inGameHelp(boolean white, boolean myTurn) {
        if (white) {
            System.out.println("\n\nYou are currently playing as white. ");
        } else {
            System.out.println("\n\nYou are currently playing as black. ");
        }
        if (myTurn) {
            System.out.println("It's your turn! Use -m to make a move or -s to show all legal moves. ");
        } else {
            System.out.println("It's not your turn yet. While you wait, you can use -s to show your possible moves. Once " +
                    "it's your turn, you can use -m to make a move. ");
        }
        System.out.println("You can also use -l to leave the game, -r to resign and admit defeat, or -d to redraw the board " +
                "if the screen is getting too cluttered. \n\n\t\t\t\t\t*\t*\t*\t\t\t\n");
    }

    public void printObserverHelp(ChessGame.TeamColor turn) {
        System.out.print("You are observing. ");
        System.out.print("It's " + turn + "'s turn. Use -s to show legal moves.\n");
        System.out.println("You can also use -l to leave the game, or -d to redraw the board anytime. \n\n\t\t\t\t\t*\t*\t*\t\t\t\n");
    }
}
