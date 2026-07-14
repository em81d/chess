package service;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.reqRes.ClearRequest;
import service.reqRes.ClearResult;

import java.util.Objects;

public class ClearService {


    private final GameDAO gameDao;
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public ClearService(GameDAO gameDao, UserDAO userDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public ClearResult clear(ClearRequest clearRequest) {
        gameDao.clearGames();
        userDao.clearUsers();
        authDao.clearAuths();
        return new ClearResult(200);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClearService that = (ClearService) o;
        return Objects.equals(gameDao, that.gameDao) && Objects.equals(userDao, that.userDao) && Objects.equals(authDao, that.authDao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameDao, userDao, authDao);
    }

    @Override
    public String toString() {
        return "ClearService{" +
                "gameDao=" + gameDao +
                ", userDao=" + userDao +
                ", authDao=" + authDao +
                '}';
    }
}
