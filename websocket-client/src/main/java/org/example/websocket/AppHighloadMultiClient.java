package org.example.websocket;

import jakarta.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.websocket.clients.WebsocketSlowClient;
import org.glassfish.tyrus.client.ClientManager;

public class AppHighloadMultiClient {

    private static final Logger logger = LogManager.getLogger(AppHighloadMultiClient.class);

    //create 10000 clients and each client send 10000 requests
    public static void main(String[] args) {
        ClientManager client = ClientManager.createClient();
        try {
            URI uri = new URI("ws://localhost:8033/webs/app");

            for (int i = 0; i <10000; i++){
                client.connectToServer(WebsocketSlowClient.class, uri);
            }

            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            bufferRead.readLine();
        } catch (URISyntaxException | DeploymentException | IOException e) {
            logger.error("A problem with highload clients", e);
        }
    }
}
