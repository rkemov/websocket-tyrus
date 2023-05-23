package org.example.websocket.clients;

import jakarta.websocket.ClientEndpoint;
import java.io.BufferedReader;
import java.io.IOException;


@ClientEndpoint
public class SimpleWebsocketClient extends WebsocketClient {

    public SimpleWebsocketClient() {
        this.clientId = "C1";
    }

    @Override
    public String initAction(String message, BufferedReader bufferRead) throws IOException {
        return bufferRead.readLine();
    }
}
