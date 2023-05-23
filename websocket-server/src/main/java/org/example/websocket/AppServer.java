package org.example.websocket;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.server.Server;

/**
 * Run server
 */
public class AppServer {
    private static final Logger logger = LogManager.getLogger(AppServer.class);

    public static void main (String[] args) {
        Server server = new Server("localhost", 8033, "/webs", null, RandomServerEndpoint.class);
        try {
            server.start();
            logger.info("+++ server is running");
            logger.info("+++ press any key to stop the server");
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            bufferRead.readLine();
        } catch (Exception e) {
            logger.error("Server fault", e);
        } finally {
            server.stop();
        }
    }

}
