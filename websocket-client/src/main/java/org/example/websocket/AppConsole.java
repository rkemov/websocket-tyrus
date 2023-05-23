package org.example.websocket;

import jakarta.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.websocket.clients.SimpleWebsocketClient;
import org.glassfish.tyrus.client.ClientManager;


/**
 * wait your input from console
 */
public class AppConsole
{
    private static final Logger logger = LogManager.getLogger(AppConsole.class);
    private static CountDownLatch latch;


    public static void main(String[] args) {
        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
        try {
            URI uri = new URI("ws://localhost:8033/webs/app");
            client.connectToServer(SimpleWebsocketClient.class, uri);
            logger.info("PRESS ANY KEY FOR CONTINUE !)");
            latch.await();
        } catch (URISyntaxException | InterruptedException | DeploymentException | IOException e) {
            logger.error("A problem with console client", e);
        }
    }
}
