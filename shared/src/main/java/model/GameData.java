package model;
import com.google.gson.annotations.Expose;
import chess.ChessGame;

public record GameData (@Expose int gameID, @Expose String whiteUsername, @Expose String blackUsername, @Expose String gameName, ChessGame game) {

}
