package at.sheldor5.jnet.server;

import at.sheldor5.jnet.requestprocessors.RequestProcessor;
import at.sheldor5.jnet.requestprocessors.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Simple server to echo every requests.
 */
public class SingleThreadServer extends Server {

    private static final Logger LOGGER = LogManager.getLogger(SingleThreadServer.class.getName());

    public SingleThreadServer(final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException {
        super(paramPort, paramRequestProcessorFactory);
    }

    public final void run() {
        LOGGER.info("Server started");
        while (isRunning()) {
            try {
                final RequestProcessor requestProcessor = requestProcessorFactory.create(serverSocket.accept());
                requestProcessor.start();
            } catch (final IOException e) {
                if (running) {
                    LOGGER.error("Error handling request: {}", e.getMessage());
                }
            }
        }
        LOGGER.info("Server stopped");
    }
}
