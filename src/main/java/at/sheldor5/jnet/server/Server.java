package at.sheldor5.jnet.server;

import at.sheldor5.jnet.processors.requests.RequestProcessorFactory;
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
    private final Logger logger = LogManager.getLogger(Server.class.getName());

    /**  */
    public final ServerSocket serverSocket;

    public RequestProcessorFactory requestProcessorFactory;

    protected boolean running = true;

    protected boolean connectionTimeOutIsSet = false;
    protected int connectionTimeOut = 0;

    /**
     * Constructor to create the ServerSocket on the desired port.
     *
     * @param paramPort The port on which this server should listen. Port 0 means automatically use the next free port.
     * @throws IOException
     */
    protected Server(final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException {
        serverSocket = new ServerSocket(paramPort);
        requestProcessorFactory = paramRequestProcessorFactory;
    }

    /**
     * Returns the port on which this server is listening, 0 if server is not running.
     * Useful if the server has automatically chosen the port.
     *
     * @return
     */
    public final int getPort() {
        return (running && null != serverSocket ? serverSocket.getLocalPort() : 0);
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
            serverSocket.close();
        } catch (final IOException e) {
            logger.error("Error closing server: {}", e.getMessage());
        }
    }

    public final void setRequestProcessorFactory(final RequestProcessorFactory paramRequestProcessorFactory) {
        synchronized (requestProcessorFactory) {
            requestProcessorFactory = paramRequestProcessorFactory;
        }
    }

    public final void setConnectionTimeOut(final int paramMillis) {
        connectionTimeOutIsSet = true;
        connectionTimeOut = paramMillis;
    }

    /**
     * Handle server requests.
     */
    public abstract void run();

}
