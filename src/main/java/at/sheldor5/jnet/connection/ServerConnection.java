package at.sheldor5.jnet.connection;

import at.sheldor5.jnet.processors.requests.EmptyRequestProcessor;
import at.sheldor5.jnet.processors.requests.EmptyRequestProcessorFactory;
import at.sheldor5.jnet.processors.requests.RequestProcessor;
import at.sheldor5.jnet.processors.requests.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 *
 * Server side connection to handle incoming requests.
 */
public class ServerConnection extends Connection {

    private final Logger logger = LogManager.getLogger(ServerConnection.class.getName());

    private RequestProcessor requestProcessor;

    public ServerConnection() {
        this(null);
    }

    public ServerConnection(final Socket paramSocket) {
        this(paramSocket, new EmptyRequestProcessorFactory());
    }

    public ServerConnection(final Socket paramSocket, final RequestProcessorFactory paramRequestProcessorFactory) {
        super(paramSocket);
        setLogger(logger);
        setRequestProcessorFactory(paramRequestProcessorFactory);
    }

    public ServerConnection(final String paramHost, final int paramPort) {
        this(paramHost, paramPort, new EmptyRequestProcessorFactory());
    }

    public ServerConnection(final String paramHost, final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) {
        super(paramHost, paramPort);
        setLogger(logger);
        setRequestProcessorFactory(paramRequestProcessorFactory);
    }

    @Override
    public final void manageConnection() {
        final String request = receive();
        if (request == null) {
            connected = false;
        } else {
            transmit(requestProcessor.process(request));
        }
    }

    public final void setRequestProcessorFactory(final RequestProcessorFactory paramRequestProcessorFactory) {
        if (null != paramRequestProcessorFactory) {
            requestProcessor = paramRequestProcessorFactory.create();
        }
    }

    public final void closeConnection() {
        connected = false;
    }
}