package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Simple server to echo every requests.
 */
public class EchoServer extends Server {

    private static final Logger LOGGER = LogManager.getLogger(EchoServer.class.getName());

    private static EchoServer instance = null;

    private EchoServer(final int paramPort) throws IOException {
        super(paramPort);
    }

    public final static EchoServer getInstance(final int paramPort) {
        if (null == instance) {
            try {
                instance = new EchoServer(paramPort);
            } catch (final IOException e) {
                LOGGER.error("Error instancing Server: {}", e.getMessage());
            }
        }
        return instance;
    }

    public final void run() {
        LOGGER.info("Server started");
        final ServerConnection connection = new ServerConnection();
        while (isRunning()) {
            try {
                connection.connect(server.accept());
                connection.sendResponse(connection.getRequest());
            } catch (final IOException e) {
                if (isRunning()) {
                    LOGGER.error("Error handling request: {}", e.getMessage());
                }
            }
        }
        LOGGER.info("Server stopped");
    }
}
