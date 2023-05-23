package org.example.websocket.clients;

import jakarta.websocket.ClientEndpoint;
import java.util.concurrent.TimeUnit;

/**
 * sleep 1 second after each request
 */
@ClientEndpoint
public class WebsocketSlowClient extends WebsocketClient {

    public WebsocketSlowClient() {
        this.clientId = "C3";
    }

    @Override
    public void sleep(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }

}
