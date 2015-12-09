package at.sheldor5.jnet.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public class SingleRequestConnection extends BasicConnection {

    public static final Logger LOGGER = LogManager.getLogger(SingleRequestConnection.class.getName());

    private static final boolean closeAfterRead = true;
    private static final boolean closeAfterWrite = false;

    public SingleRequestConnection(final Socket paramSocket) {
        super(paramSocket, closeAfterRead, closeAfterWrite);
    }

    public SingleRequestConnection(final String paramHost, int paramPort) throws IOException {
        super(paramHost, paramPort, closeAfterRead, closeAfterWrite);
    }

    public synchronized final String getResponse(final String paramRequest) {
        transmit(paramRequest);
        return receive();
    }
}
