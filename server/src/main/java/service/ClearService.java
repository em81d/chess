package service;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.RR.ClearRequest;
import service.RR.ClearResult;

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
}
