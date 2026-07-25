package client;

import exceptions.DataAccessException;
import exceptions.ServerResponseException;
import model.GameData;
import reqres.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
            System.out.println("caught you ;)\n " + e.getMessage());
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
            postLoginRepl(auth);
        }
        catch (DataAccessException e) {
            System.out.println("Caught ya ;)\n" + e.getMessage() + "  code: " + e.getCode()); //implement real error handling later
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
        }
        catch (Exception e) {
            System.out.println("caught you ;) couldn't log out\n " + e.getMessage());
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
            System.out.println("Caught ya ;) couldn't create \n " + e.getMessage());
        }
    }

    public void listGames(String auth) {
        try {
            Collection<GameData> games = server.listGames(new ListRequest(auth)).games();
            int gameNumber = 1;
            for (GameData game : games) {
                System.out.println(gameNumber + ".\tName: " + game.gameName() + "\n\tPlaying as white: " +
                        printWhiteUsername(game) + "\n\tPlaying as black: " + printBlackUsername(game) + '\n');
                gameNumber++;
            }
        }
        catch (ServerResponseException e) {
            System.out.println("Caught ya ;) couldn't list\n " + e.getMessage());
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
            int gameId = Integer.parseInt(scanner.nextLine());


            try {
                System.out.print("BLACK or WHITE: ");
                String color = scanner.nextLine();
                server.join(new JoinRequest(auth, color, gameId));
                postJoinRepl();
            }
            catch (ServerResponseException e) {
                System.out.println("Try again! It's possible that that game does not exist, that spot is taken," +
                        " or your color was invalid.");
            }
        }
        catch (Exception e) {
            System.out.println("Game id should be an integer.");
        }

    }

    public void observeGame(String auth, Scanner scanner) {
        try {
            System.out.println("Which game would you like to observe? Game number: ");
            int gameID = Integer.parseInt(scanner.nextLine());
            observeRepl();
        }
        catch (Exception e) {
            System.out.println("invalid game id!");
        }
    }

    public void postJoinRepl() {
//        drawBoard(true, )
    }

    public void observeRepl() {

    }

}
