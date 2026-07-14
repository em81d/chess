package service.reqRes;
import model.AbbreviatedGame;
import java.util.Collection;

public record ListResult(Collection<AbbreviatedGame> games, int status) {
}
