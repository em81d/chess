package client;

import exceptions.DataAccessException;
import exceptions.ServerResponseException;
import reqres.LoginRequest;
import reqres.RegisterRequest;
import server.ServerFacade;

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
                    printHelpDialog();
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
            }
        }

        scanner.close();
    }

    public void printHelpDialog() {
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
            server.login(new LoginRequest(username, password));
        }
        catch (Exception e) {
            System.out.println("caught you ;)\n " + e.getMessage());
        }

        postLoginRepl();

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
            server.register(new RegisterRequest(username, password, email));
        }
        catch (DataAccessException e) {
            System.out.println("Caught ya ;)\n" + e.getMessage() + "  code: " + e.getCode()); //implement real error handling later
        }

        postLoginRepl();
    }


    public void postLoginRepl() {

    }

}
