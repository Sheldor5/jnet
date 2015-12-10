package at.sheldor5.jnet.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 *
 * Simple connection object for clients.
 * Receives the response of the desired request and closes the socket.
 */
public class ClientConnection extends Connection {

    private static final Logger LOGGER = LogManager.getLogger(ClientConnection.class.getName());

    public ClientConnection() {
        super(null);
    }

    protected ClientConnection(String paramHost, int paramPort) throws IOException {
        super(paramHost, paramPort);
    }

    protected ClientConnection(Socket paramSocket) {
        super(paramSocket);
    }

    @Override
    protected boolean onTimeOut(SocketTimeoutException e) {
        LOGGER.error("Error reading data: reader timed out");
        return true;
    }

    public final String getResponse(final String paramRequest) {
        transmit(paramRequest);
        final String response = receive();
        close();
        return response;
    }
}
