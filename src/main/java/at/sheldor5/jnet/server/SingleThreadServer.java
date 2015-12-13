package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.ServerConnection;
import at.sheldor5.jnet.processors.requests.RequestProcessor;
import at.sheldor5.jnet.processors.requests.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Simple server to echo every requests.
 */
public class SingleThreadServer extends Server {

    private final Logger logger = LogManager.getLogger(SingleThreadServer.class.getName());

    private final RequestProcessor requestProcessor;

    public SingleThreadServer(final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException {
        super(paramPort, paramRequestProcessorFactory);
        requestProcessor = requestProcessorFactory.create();
    }

    public final void run() {
        logger.info("Server started");
        final ServerConnection connection = new ServerConnection(null, requestProcessorFactory);
        while (running) {
            try {
                final Socket client = serverSocket.accept();
                logger.info("New client accepted");
                if (connectionTimeOutIsSet) {
                    client.setSoTimeout(connectionTimeOut);
                }
                connection.connect(client);
                while (connection.isConnected()) {
                    connection.manageConnection();
                }
            } catch (final Exception e) {
                if (running) {
                    logger.error("Error handling request: {}", e.getMessage());
                }
            }
        }
        logger.info("Server stopped");
    }
}
