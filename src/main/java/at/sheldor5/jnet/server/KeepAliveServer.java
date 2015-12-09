package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.BasicConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Simple server to response to "are you still connected?" requests.
 * Responses with message "200" response will be.
 */
public class KeepAliveServer extends Server {

    public static final Logger LOGGER = LogManager.getLogger(KeepAliveServer.class.getName());

    private static KeepAliveServer instance = null;

    private KeepAliveServer(final int paramPort) throws IOException {
        super(paramPort);
    }

    public static KeepAliveServer getInstance(final int paramPort) {
        if (null == instance) {
            try {
                instance = new KeepAliveServer(paramPort);
            } catch (final IOException e) {
                LOGGER.error("Error instancing Server: {}", e.getMessage());
            }
        }
        return instance;
    }

    @Override
    public void run() {
        LOGGER.info("Server started");
        while (isRunning()) {
            try {
                final BasicConnection connection = new BasicConnection(server.accept(), false, true);
                connection.transmit("200: " + connection.receive());
            } catch (final IOException e) {
                LOGGER.error("Error handling request: {}", e.getMessage());
            }
        }
        LOGGER.info("Server stopped");
    }
}
