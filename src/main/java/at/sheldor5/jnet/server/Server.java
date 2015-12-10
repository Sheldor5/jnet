package at.sheldor5.jnet.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Abstract class for single.thread servers.
 */
public abstract class Server extends Thread {

    /** Class logger. */
    private static final Logger LOGGER = LogManager.getLogger(Server.class.getName());

    /**  */
    public final ServerSocket server;
    private boolean running = true;

    /**
     * Constructor to create the ServerSocket on the desired port.
     *
     * @param paramPort The port on which this server should listen. Port 0 means automatically use the next free port.
     * @throws IOException
     */
    public Server(final int paramPort) throws IOException {
        server = new ServerSocket(paramPort);
    }

    /**
     * Returns the port on which this server is listening, 0 if server is not running.
     * Useful if the server has automatically chosen the port.
     *
     * @return
     */
    public final int getPort() {
        return (running && null != server ? server.getLocalPort() : 0);
    }

    /**
     * Returns true if the server is running, false otherwise
     *
     * @return
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Stops the server by closing it's server socket.
     */
    public final void stopServer() {
        running = false;
        try {
            server.close();
        } catch (final IOException e) {
            LOGGER.error("Error closing server: {}", e.getMessage());
        }
    }

    /**
     * Handle server requests.
     */
    public abstract void run();

}
