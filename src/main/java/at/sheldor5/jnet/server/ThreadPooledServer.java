package at.sheldor5.jnet.server;

import at.sheldor5.jnet.RequestProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public abstract class ThreadPooledServer {

    public static final Logger LOGGER = LogManager.getLogger(ThreadPooledServer.class.getName());

    private ServerSocket server = null;

    private final RequestProcessor requestProcessor;

    public ThreadPooledServer(final RequestProcessor paramRequestProcessor) {
        requestProcessor = paramRequestProcessor;
    }

    public void start(final int paramPort) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting server on port {} ...", paramPort);
        }
        if (null == this.server) {
            try {
                this.server = new ServerSocket(paramPort);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Server started");
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return ((null != this.server) && !this.server.isClosed());
    }

    public void stopServer() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Stopping server ...");
        }
        if (this.isRunning() && null != this.server) {
            if (!this.server.isClosed()) {
                try {
                    this.server.close();
                    this.server = null;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Server stopped");
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
