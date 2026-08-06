package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>(); //needs to be modified so it
    //stores ALL the sessions associated with one game id (players, observers, etc)
    //integer (game ID) maps to the list of sessions

    public void add(int gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArrayList<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void removeAll(int gameID) {
        connections.remove(gameID);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        ArrayList<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    if (!s.equals(excludeSession)) {
                        s.getRemote().sendString(msg);
                    }
                }
            }
        }
    }

    public void sendToSession(int gameID, Session session, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }

    /**
     *
     * check if any sessions are currently in progress for a game. if not, a user has resigned or the game is over.
    * */
    public boolean checkInPlay(int gameID) {
        return connections.containsKey(gameID);
    }
}