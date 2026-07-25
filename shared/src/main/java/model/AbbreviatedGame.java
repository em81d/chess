package model;

public record AbbreviatedGame (int gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        return "\n{" +
                "Name: " + gameName +
                ", ID: " + gameID +
                ", playing as white: " + whiteUsername +
                ", playing as black: " + blackUsername +
                "}";
    }
}
