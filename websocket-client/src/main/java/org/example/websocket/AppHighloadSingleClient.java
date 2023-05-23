package org.example.websocket;

import jakarta.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.websocket.clients.WebsocketClient;
import org.glassfish.tyrus.client.ClientManager;

public class AppHighloadSingleClient {
    private static final Logger logger = LogManager.getLogger(AppHighloadSingleClient.class);

    // 1 client send 10000 requests
    public static void main(String[] args) {
        ClientManager client = ClientManager.createClient();
        try {
            URI uri = new URI("ws://localhost:8033/webs/app");
            client.connectToServer(WebsocketClient.class, uri);

            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            bufferRead.readLine();
        } catch (URISyntaxException | DeploymentException | IOException e) {
            logger.error("A problem with single client", e);
        }
    }
}
