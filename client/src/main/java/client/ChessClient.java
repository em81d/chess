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

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_PAWN;

public class ChessClient implements NotificationHandler {

    private final ServerFacade server;
    private Map<Integer,Integer> gameIDs;
    private final WebSocketFacade ws;
    private final BoardDrawer drawer;

    private ChessGame.TeamColor currentColor;

    public ChessClient(String serverUrl) throws ServerResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
        currentColor = null;
        drawer = new BoardDrawer();
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
                switch (result) {
                    case "-h" -> drawer.printPreloginHelp();
                    case "-l" -> loginUser(scanner);
                    case "-r" -> registerUser(scanner);
                }
            } catch (Exception e) {
                System.out.println("invalid input.");
                System.out.println(e.getMessage());
                result = "-q";
            }
        }
        scanner.close();
    }

    public void loginUser(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            String auth = server.login(new LoginRequest(username, password)).authToken();
            postLoginRepl(auth);
        } catch (Exception e) {
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
            } else {
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
            } else if (password.length() <7) {
                System.out.println("Password is too short.");
            } else {
                passwordCreated = true;
            }
        }
        while (!emailCreated) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
            if (email.length() > 99) {
                System.out.println("Email should be under 100 characters.");
            } else if (!email.contains("@")) {
                System.out.println("Make sure your email is a valid email address.");
            } else {
                emailCreated = true;
            }
        }
        try {
            String auth = server.register(new RegisterRequest(username, password, email)).authToken();
            System.out.println("\nLogin success!");
            postLoginRepl(auth);
        } catch (DataAccessException e) {
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
                    drawer.printPostloginHelp();
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
                    ws.connect(auth, gameIDs.get(gameNumber));

                    if (color.equals("WHITE")) {
                        currentColor = WHITE;
                    }
                    else {
                        currentColor = BLACK;
                    }

                    postJoinRepl(gameIDs.get(gameNumber), auth, color.equals("WHITE"));
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
                ws.connect(auth, gameIDs.get(gameNumber));
                observeRepl(gameIDs.get(gameNumber), auth);
            }

        }
        catch (Exception e) {
            System.out.println("Invalid game number: game number should be the integer number printed next to the game you would like to join.");
        }
    }

    public void postJoinRepl(int gameID, String auth, boolean isWhite) {
        try {
            Scanner scanner = new Scanner(System.in);
            boolean playing = true;
            String input = "";

            while (playing) {

                System.out.println("-h for help \t|\t -d to draw chessboard \t|\t -l to leave game \t|\t -m to make a move" +
                        " \t|\t -r to resign \t|\t -s to show legal moves");
                input = scanner.nextLine();

                switch (input) {
                    case "-h" -> inGameHelp(isWhite, gameID, auth);
                    case "-d" -> drawGame(isWhite, gameID, auth);
                    case "-l" -> {
                        leaveGame(auth, gameID);
                        playing = false;
                    }
                    case "-m" -> {
                        makeMove(auth, gameID);
                    }
                    case "-r" -> {
                        resign(auth, gameID);
                    }
                    case "-s" -> highlight(auth, gameID, isWhite);
                }
            }
        } catch (Exception e) {
            System.out.println("invalid command. ");
        }
    }

    public void inGameHelp(boolean white, int gameID, String auth) {
        try {
            GameData game = getUpdatedGame(auth, gameID);
            drawer.inGameHelp(white, (game.game().getTeamTurn() == WHITE) == white); //third parameter is true if your turn
        }
        catch (ServerResponseException e) {
            System.out.println("Error getting current game state. See commands below.");
        }
    }

    public void makeMove(String auth, int id) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("\nPositions should be a lowercase letter designating the column, followed immediately by a single" +
                    " digit designating the row.\n");
            System.out.print("Position you are moving from: ");
            String pos1 = input.nextLine();
            System.out.print("Position you are moving to: ");
            String pos2 = input.nextLine();
            System.out.print("Do you need to promote a pawn with this move? ");
            String promotionQuestion = input.nextLine();
            String promotion = null;
            if (promotionQuestion.contains("y")) {
                System.out.print("QUEEN, BISHOP, KNIGHT, or ROOK: ");
                String promotionAnswer = input.nextLine();
                switch (promotionAnswer) {
                    case "QUEEN" -> promotion = "QUEEN";
                    case "BISHOP" -> promotion = "BISHOP";
                    case "KNIGHT" -> promotion = "KNIGHT";
                    case "ROOK" -> promotion = "ROOK";
                }
            }
            try {
                ws.move(auth, id, pos1, pos2, promotion);
            } catch (ServerResponseException e) {
                System.out.println(SET_TEXT_COLOR_RED + "\nInvalid move. " + RESET_TEXT_COLOR);
            }

        }
        catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + "\nInvalid command. " + RESET_TEXT_COLOR);
        }

    }

    public void drawGame(boolean isWhite, int gameID, String auth) {
        try {
            GameData game = getUpdatedGame(auth, gameID);
            drawer.drawBoard(isWhite, game.game(), new ArrayList<>());
        } catch (ServerResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Error getting current game state." + RESET_TEXT_COLOR);
        }
    }

    public void highlight(String auth, int gameID, boolean isWhite) {
        try {
            GameData game = getUpdatedGame(auth, gameID);

            Scanner input = new Scanner(System.in);
            System.out.println("\nPositions should be a lowercase letter designating the column, followed immediately by a single" +
                    " digit designating the row.\n");
            System.out.print("Position of the piece whose moves you want to highlight: ");
            String pos = input.nextLine();
            ChessPosition p = ws.toPosition(pos);

            Collection<ChessMove> moves = game.game().validMoves(p);
            Collection<ChessPosition> positions = new ArrayList<>();

            for (ChessMove move : moves) {
                positions.add(move.getEndPosition());
            }
            positions.add(p); //includes starting position of piece

            drawer.drawBoard(isWhite, game.game(), positions);

        } catch (ServerResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Error getting current game state." + RESET_TEXT_COLOR);
        } catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + "Invalid command." + RESET_TEXT_COLOR);
        }
    }

    public void resign(String auth, int gameID) {

        try {
            Scanner in = new Scanner(System.in);
            System.out.print(SET_TEXT_COLOR_BLUE + "Are you sure you want to resign? " + RESET_TEXT_COLOR);
            String result = in.nextLine();
            if (result.equalsIgnoreCase("y") || result.equalsIgnoreCase("yes")) {
                ws.resign(auth, gameID);
                System.out.println("Thanks for playing! Better luck next time.");
            }
        } catch (ServerResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Unable to connect to server to resign." + RESET_TEXT_COLOR);
        }

    }

    public void leaveGame(String auth, int gameID) {
        try {
            ws.leave(auth, gameID);
            System.out.println("You left the game. Thanks for playing! Come play again soon!");
        } catch (ServerResponseException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Unable to connect to server to leave." + RESET_TEXT_COLOR);
        }
    }

    public void observeRepl(int gameID, String auth) {
        try {
            Scanner scanner = new Scanner(System.in);
            boolean playing = true;
            String input = "";

            while (playing) {

                System.out.println("-h for help \t|\t -d to draw chessboard \t|\t -l to leave game " +
                        "\t|\t -s to show legal moves");
                input = scanner.nextLine();

                switch (input) {
                    case "-h" -> observerHelp(gameID, auth);
                    case "-d" -> drawGame(true, gameID, auth);
                    case "-l" -> {
                        leaveGame(auth, gameID);
                        playing = false;
                    }
                    case "-s" -> highlight(auth, gameID, true);
                }
            }
        } catch (Exception e) {
            System.out.println("invalid command. ");
        }
    }

    public void observerHelp(int gameId, String auth) {
        try {
            GameData game = getUpdatedGame(auth, gameId);
            drawer.printObserverHelp(game.game().getTeamTurn());
        } catch (ServerResponseException e) {
            System.out.print("Error getting current game state. See commands below.");
        }
    }

    public GameData getUpdatedGame(String auth, int gameID) throws ServerResponseException {
        GameData game = null;
        for (GameData g : server.listGames(new ListRequest(auth)).games()) {
            if (g.gameID() == gameID) {
                game = g;
            }
        }
        if (game == null) {
            throw new ServerResponseException("Game id not found.");
        }
        return game;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> System.out.println(SET_TEXT_COLOR_MAGENTA + ((NotificationMessage) message).getMessage() + RESET_TEXT_COLOR);
            case ERROR -> System.out.println(SET_TEXT_COLOR_RED + ((ErrorMessage) message).getErrorMessage() + RESET_TEXT_COLOR);
            case LOAD_GAME -> {
                System.out.println(SET_TEXT_COLOR_GREEN + "Game updated." + RESET_TEXT_COLOR);
                GameData game = ((LoadGameMessage) message).getGame();
                drawer.drawBoard(currentColor == WHITE || currentColor == null, game.game(), new ArrayList<>());
            }
        }
    }
}