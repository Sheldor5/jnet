package at.sheldor5.jnet.connection;

import at.sheldor5.jnet.requestprocessors.EmptyRequestProcessor;
import at.sheldor5.jnet.requestprocessors.DataProcessor;
import at.sheldor5.jnet.requestprocessors.RequestProcessorFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 *
 * Simple connection object for servers.
 * Receives the request and responses with the desired response and closes the socket.
 */
public class ServerConnection extends Connection {

    private DataProcessor requestProcessor;

    public ServerConnection() {
        this(null);
    }

    public ServerConnection(final RequestProcessorFactory paramRequestProcessorFactory) {
        this(null, paramRequestProcessorFactory);
    }

    public ServerConnection(final Socket paramSocket, final RequestProcessorFactory paramRequestProcessorFactory) {
        super(paramSocket);
        if (null == paramRequestProcessorFactory) {
            requestProcessor = new EmptyRequestProcessor();
        } else {
            requestProcessor = paramRequestProcessorFactory.create();
        }
    }

    public ServerConnection(final String paramHost, final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException {
        super(paramHost, paramPort);
        if (null == paramRequestProcessorFactory) {
            requestProcessor = new EmptyRequestProcessor();
        } else {
            requestProcessor = paramRequestProcessorFactory.create();
        }
    }

    @Override
    public void manageConnection() {
        while (processNextRequest()) {
            //
        }
    }

    public final synchronized boolean processNextRequest() {
        final String request = receive();
        if (request != null) {
            transmit(requestProcessor.process(request));
            return true;
        }
        return false;
    }

    public final void setRequestProcessorFactory(final RequestProcessorFactory paramRequestProcessorFactory) {
        if (null != paramRequestProcessorFactory) {
            requestProcessor = paramRequestProcessorFactory.create();
        }
    }

    public final void closeConnection() {
        close();
    }
}