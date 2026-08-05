package client;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exceptions.DataAccessException;
import exceptions.ServerResponseException;
import model.GameData;
import reqres.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_PAWN;


public class ChessClient implements NotificationHandler {


    private final ServerFacade server;
    private Map<Integer,Integer> gameIDs;
    private final WebSocketFacade ws;


    public ChessClient(String serverUrl) throws ServerResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to chess!");

        preLoginRepl();

    }


    public void preLoginRepl() {
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("-q")) {
            System.out.println("-h for help \t|\t -q to quit \t|\t -l to login \t|\t -r to register ");

            try {
                result = scanner.nextLine();

                if (result.equals("-h")) {
                    printPreloginHelp();
                }
                else if (result.equals("-l")) {
                    loginUser(scanner);
                }
                else if (result.equals("-r")) {
                    registerUser(scanner);
                }
            }
            catch (Exception e) {
                System.out.println("invalid input.");
                System.out.println(e);
                result = "-q";

            }
        }

        scanner.close();
    }

    public void printPreloginHelp() {
        System.out.println("To use the chess application, start by logging in or creating an account!");
        System.out.println("Type -l and hit enter to log in, or type -r and hit enter to create an account.");
        System.out.println("Once you've entered a command, the terminal will walk you through the next steps.");
        System.out.println("Then you will get to the next part of the application, where you will be able to play chess!");

        System.out.println("\n\t\t\t\t\t\t*\t*\t*\t\t\t\n");
    }

    public void loginUser(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            String auth = server.login(new LoginRequest(username, password)).authToken();
            postLoginRepl(auth);
        }
        catch (Exception e) {
            System.out.println("Login failed. Incorrect username or password");
        }

    }

    public void registerUser(Scanner scanner) {
        boolean usernameCreated = false;
        String username = "";
        boolean passwordCreated = false;
        String password = "";
        boolean emailCreated = false;
        String email = "";

        System.out.println("Nice to meet you, glad you're here!");
        while (!usernameCreated) {
            System.out.print("Create a username: ");
            username = scanner.nextLine();
            if (username.length() > 99) {
                System.out.println("Username should be under 100 characters.");
            }
            else {
                usernameCreated = true;
            }
        }

        while (!passwordCreated) {
            System.out.println("Create a password. Passwords should be at least 6 characters and include a variety of " +
                    "letters, numbers, and symbols.");
            System.out.print("Your password: ");
            password = scanner.nextLine();
            if (password.length() > 99) {
                System.out.println("Password should be under 100 characters.");
            }
            else if (password.length() <7) {
                System.out.println("Password is too short.");
            }
            else {
                passwordCreated = true;
            }
        }

        while (!emailCreated) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
            if (email.length() > 99) {
                System.out.println("Email should be under 100 characters.");
            }
            else if (!email.contains("@")) {
                System.out.println("Make sure your email is a valid email address.");
            }
            else {
                emailCreated = true;
            }
        }

        try {
            String auth = server.register(new RegisterRequest(username, password, email)).authToken();
            System.out.println("\nLogin success!");
            postLoginRepl(auth);
        }
        catch (DataAccessException e) {
            System.out.println("Username is already taken! Try a different one.\n"); //implement real error handling later
        }

    }


    public void postLoginRepl(String auth) {
        String result = "";
        Scanner scanner = new Scanner(System.in);

        while (!result.equals("-e")) {
            System.out.println("\n-h for help \t|\t -e to exit and logout \t|\t -c to create a game \t|\t -l to list existing " +
                    "games \t|\t -p to play a game \t|\t -o to observe a game ");

            try {
                result = scanner.nextLine();

                if (result.equals("-h")) {
                    printPostloginHelp();
                }
                else if (result.equals("-e")) {
                    logoutUser(auth);
                }
                else if (result.equals("-c")) {
                    createGame(auth, scanner);
                }
                else if (result.equals("-l")) {
                    listGames(auth);
                }
                else if (result.equals("-p")) {
                    joinGame(auth, scanner);
                }
                else if (result.equals("-o")) {
                    observeGame(auth, scanner);
                }
                else {
                    System.out.println("invalid input.");
                }
            }
            catch (Exception e) {
                System.out.println("invalid input.");
                result = "-e";
            }
        }

//        scanner.close();
    }

    public void printPostloginHelp() {

        System.out.println("\nWelcome to your account homepage! Start by hitting -l to see what games have already\n been created," +
                " and then if you'd like you can join one to play with -p! When you join a game, you \nwill need the game id that " +
                "is printed during the list dialog. If you aren't feeling ready to \njoin a game just yet, you can also pick a game id" +
                "and join as an observer with -o. If there isn't \nalready a game you want to join, create one with -c. When you're " +
                "all done playing, it's a simple -e \nto exit and log out. Happy chess playing!\n\t\t\t\t\t\t*****\t\t\t");
    }

    public void logoutUser(String auth) {
        try {
            server.logout(new LogoutRequest(auth));
            System.out.println("\nLogout success.\n");
        }
        catch (Exception e) {
            System.out.println("Unable to log out.");
        }
    }

    public void createGame(String auth, Scanner scanner) {
        try {
            String name = "";
            while (name.isEmpty()) {
                System.out.print("What would you like to name your game? ");
                name = scanner.nextLine();
                if (name.length() > 99) {
                    name = "";
                    System.out.println("Your game's name needs to be less than 100 characters.");
                }
            }
            server.create(new CreateRequest(auth, name));
        }
        catch (Exception e) {
            System.out.println("Unable to create game.");
        }
    }

    public void listGames(String auth) {
        try {
            Collection<GameData> games = server.listGames(new ListRequest(auth)).games();
            gameIDs = new HashMap<>();

            int gameNumber = 1;
            for (GameData game : games) {
                System.out.println(gameNumber + ".\tName: " + game.gameName() + "\n\tPlaying as white: " +
                        printWhiteUsername(game) + "\n\tPlaying as black: " + printBlackUsername(game) + '\n');

                //store which game number corresponds to which game, fresh each time games are listed
                gameIDs.put(gameNumber, game.gameID());

                gameNumber++;
            }
            if (games.isEmpty()) {
                System.out.println("\nNo games to display.\n");
            }
        }
        catch (ServerResponseException e) {
            System.out.println("Unable to list games.");
        }
    }

    public String printWhiteUsername(GameData game) {
        if (game.whiteUsername() == null) {
            return "________";
        }
        else {
            return game.whiteUsername();
        }
    }

    public String printBlackUsername(GameData game) {
        if (game.blackUsername() == null) {
            return "________";
        }
        else {
            return game.blackUsername();
        }
    }

    public void joinGame(String auth, Scanner scanner) {
        try {
            System.out.print("Which game would you like to join? Game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            if (!gameIDs.containsKey(gameNumber)) {
                System.out.println("Not a valid game number. Try listing the games first.");
            }
            else {
                try {
                    System.out.print("BLACK or WHITE: ");
                    String color = scanner.nextLine();
                    server.join(new JoinRequest(auth, color, gameIDs.get(gameNumber)));
                    ws.playGame(auth);                                                    // ???
                    postJoinRepl(color.equals("WHITE"));
                }
                catch (ServerResponseException e) {
                    System.out.println("Try again! It's possible that that game does not exist, that spot is taken," +
                            " or your color was invalid.");
                }
            }

        }
        catch (Exception e) {
            System.out.println("Invalid game number: game number should be the integer number printed next to the game you would like to join.");
        }

    }

    public void observeGame(String auth, Scanner scanner) {
        try {
            System.out.println("Which game would you like to observe? Game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            if (!gameIDs.containsKey(gameNumber)) {
                System.out.println("Not a valid game number. Try listing the games first.");
            }
            else {
                ws.observeGame(auth);
                observeRepl(gameIDs.get(gameNumber));
            }

        }
        catch (Exception e) {
            System.out.println("Invalid game number: game number should be the integer number printed next to the game you would like to join.");
        }
    }

    public void postJoinRepl(boolean isWhite) {
        drawBoard(isWhite, new ChessGame());
    }

    public void observeRepl(int gameNumber) {
        drawBoard(true, new ChessGame());
    }


    public void drawBoard(boolean isWhite, ChessGame game) {
        ChessBoard board = game.getBoard();
        System.out.print("\u001b[49;38;2;127;161;124;1m");
//        System.out.print("\u001b[35;105;1m 1 ");

        if (isWhite) {

            System.out.println("    a  b  c  d  e  f  g  h   \u001b[49m");

            ChessPosition pos;
            for (int i=8; i>0; i--) {
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + " \u001b[30m");
                for (int j=1; j<9; j++) {
                    printSquare(i,j,board);
                }
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + "  \u001b[49m\n");
            }
            System.out.print("\u001b[49;38;2;127;161;124;1m");
            System.out.println("    a  b  c  d  e  f  g  h   \u001b[49m");
        }
        else {
            System.out.println("    h  g  f  e  d  c  b  a   \u001b[49m");

            ChessPosition pos;
            for (int i=1; i<9; i++) {
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + " \u001b[30m");
                for (int j=8; j>0; j--) {
                    printSquare(i,j,board);
                }
                System.out.print("\u001b[49;38;2;127;161;124;1m " + i + "  \u001b[49m\n");
            }
            System.out.print("\u001b[49;38;2;127;161;124;1m");
            System.out.println("    h  g  f  e  d  c  b  a   \u001b[49m");
        }
        System.out.print("\u001b[39m");

    }

    public void printSquare(int i, int j, ChessBoard board)  {
        ChessPosition pos = new ChessPosition(i,j);
        if (i % 2 != j % 2) {
            //white square
            System.out.print("\u001b[48;2;214;191;206m");
        }
        else {
            //black square
            System.out.print("\u001b[48;2;128;102;119m");
        }
        if (board.getPiece(pos) == null) {
            System.out.print(EMPTY);
        }
        else {
            printPiece(board.getPiece(pos));
        }
    }

    public void printPiece(ChessPiece piece) {
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

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> System.out.println(SET_TEXT_COLOR_MAGENTA + ((NotificationMessage) message).getMessage() + RESET_TEXT_COLOR);
            case ERROR -> System.out.println(SET_TEXT_COLOR_RED + ((ErrorMessage) message).getErrorMessage() + RESET_TEXT_COLOR);
            case LOAD_GAME -> {
                System.out.println(SET_TEXT_COLOR_GREEN + "Game updated." + RESET_TEXT_COLOR);
                GameData game = ((LoadGameMessage) message).getGame();
                //how to tell if this client is player/observer, black/white?
                drawBoard(true, game.game());

            }
        }


    }


}
