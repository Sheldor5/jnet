package at.sheldor5.jnet.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public class BasicConnection extends Connection {

    public static final Logger LOGGER = LogManager.getLogger(BasicConnection.class.getName());

    public BasicConnection(final Socket paramSocket, final boolean paramCloseAfterRead, final boolean paramCloseAfterWrite) {
        super(paramSocket, paramCloseAfterRead, paramCloseAfterWrite);
    }

    public BasicConnection(final String paramHost, final int paramPort, final boolean paramCloseAfterRead, final boolean paramCloseAfterWrite) throws IOException {
        super(paramHost, paramPort, paramCloseAfterRead, paramCloseAfterWrite);
    }

    @Override
    public final boolean onTimeOut(SocketTimeoutException e) {
        LOGGER.error("Error reading data: reader timed out");
        return true;
    }
}
