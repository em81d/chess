package server.websocket;

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

    public void broadcast(int gameID, Session excludeSession, NotificationMessage notification) throws IOException {
        String msg = notification.getMessage();
        ArrayList<Session> sessions = connections.get(gameID);
        for (Session s : sessions) {
            if (s.isOpen()) {
                if (!s.equals(excludeSession)) {
                    s.getRemote().sendString(msg);
                }
            }
        }
    }
}