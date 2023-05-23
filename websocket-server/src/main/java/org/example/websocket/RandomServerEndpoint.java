package org.example.websocket;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Hashtable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.websocket.service.RandomSyncService;
import org.glassfish.tyrus.core.TyrusSession;

/**
 * 1 instance per client
 */
@ServerEndpoint(value = "/app")
public class RandomServerEndpoint {

    private static final Logger logger = LogManager.getLogger(RandomServerEndpoint.class);
    private static final RandomSyncService randomSyncService = new RandomSyncService();

    // Hashtable is not enough for synchronization between method calls
    private static final Hashtable<String, String> occupiedIP2SessionId = new Hashtable<>();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        String ip = "";
        ip = getIP(session);
        //check uniq IP
        synchronized (occupiedIP2SessionId) { //  synchronization between method calls
            if (occupiedIP2SessionId.containsKey(ip)) {
                abortConnection(session, ip);
            } else {
                occupiedIP2SessionId.put(ip, session.getId());
            }
        }
        logger.info("Connected, ip = {}, sessionID = {}", ip, session.getId());
    }

    private static String getIP(Session session) throws Exception {
        String ip;
        if(session instanceof TyrusSession tyrusSession){
            ip = tyrusSession.getRemoteAddr();
        } else {
            logger.error("Strange client session: {}", session);
            throw new Exception("Client without ip address !");
        }
        return ip;
    }

    private static void abortConnection(Session session, String ip) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Forbidden!"));
            logger.error("Client with ip: {}, try to connect again !", ip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        logger.debug("I got request: " + message);
        if (message.equals("quit")) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye!"));
            } catch (IOException e) {
                logger.info("Client want to exit", e);
            }
        }
        BigInteger id = randomSyncService.generateSyncronizedId();
        //too simple json, so using jackson is not necessary. We cutback resources
        return "{\"val\": " + id + "}";
    }



    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Session " + session.getId() + " closed because " + closeReason);
        try {
            synchronized (occupiedIP2SessionId) { //  synchronization between method calls
                String ip = getIP(session);
                String legalSessionId = occupiedIP2SessionId.get(ip);
                if(legalSessionId == null){
                    logger.error("Illegal client detected ! {}", session);
                }
                if (session.getId().equals(legalSessionId)) {
                    occupiedIP2SessionId.remove(ip);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}