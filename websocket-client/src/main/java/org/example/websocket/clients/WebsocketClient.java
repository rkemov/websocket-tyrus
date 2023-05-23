package org.example.websocket.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientEndpoint
public class WebsocketClient {

    private static final Logger logger = LogManager.getLogger(WebsocketClient.class);
    protected String clientId = "C2";

    private int callsLimit = 10000;
    private int current = 0;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        logger.info("+++ Connected " + session.getId());
        try {
            session.getBasicRemote().sendText("start from " + clientId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            current++;
            Response response = objectMapper.readValue(message, Response.class);
            logger.info(clientId + " received target val: " + response.getVal());
            //simulate long calculations, if you need
            sleep(1);
            String userInput = initAction(message, bufferRead);
            return userInput;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String initAction(String message, BufferedReader bufferRead) throws IOException {
        String userInput = "request" + current;
        //we send callsLimit requests to server, then wait user input
        if(current> callsLimit) {
            userInput = bufferRead.readLine();
        }
        return userInput;
    }

    public void sleep(int seconds) throws InterruptedException {
        //fast client
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Session " + session.getId() + " closed because " + closeReason);
    }


}
