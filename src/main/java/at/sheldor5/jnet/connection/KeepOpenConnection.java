package at.sheldor5.jnet.connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 *
 * Handle basic read/write operations on sockets.
 */
public class KeepOpenConnection extends Connection {

    public static final Logger LOGGER = LogManager.getLogger(KeepOpenConnection.class.getName());

    private static final boolean closeAfterRead = false;
    private static final boolean closeAfterWrite = false;

    private int toc = 0;

    public KeepOpenConnection(final Socket paramSocket, final int paramTimeOutMillis) {
        super(paramSocket, closeAfterRead, closeAfterWrite);
        setTimeOut(paramTimeOutMillis);
    }

    public KeepOpenConnection(final String paramHost, int paramPort, final int paramTimeOutMillis) throws IOException {
        super(paramHost, paramPort, closeAfterRead, closeAfterWrite);
        setTimeOut(paramTimeOutMillis);
    }

    @Override
    public boolean onTimeOut(final SocketTimeoutException e) {
        LOGGER.error("Connection timed out: {}", e.getMessage());
        if (++toc >= 3) {
            return true;
        }
        return false;
    }

    public synchronized final String getResponse(final String paramRequest) {
        transmit(paramRequest);
        return receive();
    }

    public synchronized final String getRequest() {
        return receive();
    }

    public synchronized final void sendResponse(final String paramResponse) {
        transmit(paramResponse);
    }

    public final void setTimeOutCounter(final int paramTimeOutCount) {
        toc = paramTimeOutCount;
    }

    public final void resetTimeOutCounter() {
        toc = 0;
    }

}
