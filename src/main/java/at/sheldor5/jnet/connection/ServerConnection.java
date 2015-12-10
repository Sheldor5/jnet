package at.sheldor5.jnet.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 *
 * Simple connection object for servers.
 * Receives the request and responses with the desired response and closes the socket.
 */
public class ServerConnection extends Connection {

    private static final Logger LOGGER = LogManager.getLogger(ServerConnection.class.getName());

    public ServerConnection() {
        super(null);
    }

    public ServerConnection(final Socket paramSocket) {
        super(paramSocket);
    }

    public ServerConnection(final String paramHost, final int paramPort) throws IOException {
        super(paramHost, paramPort);
    }

    @Override
    protected final boolean onTimeOut(SocketTimeoutException e) {
        LOGGER.error("Error reading data: reader timed out");
        return true;
    }

    public final String getRequest() {
        return receive();
    }

    public final void sendResponse(final String paramResponse) {
        transmit(paramResponse);
        close();
    }
}