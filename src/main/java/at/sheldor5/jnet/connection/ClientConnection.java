package at.sheldor5.jnet.connection;

import at.sheldor5.jnet.processors.responses.AcceptAllResponseProcessorFactory;
import at.sheldor5.jnet.processors.responses.ResponseProcessor;
import at.sheldor5.jnet.processors.responses.ResponseProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Michael Palata <a href="https://github.com/Sheldor5">@github.com/Sheldor5</a> on 10.12.2015.
 *
 * Client side connection to send data to a server.
 */
public class ClientConnection extends Connection {

    private final Logger logger = LogManager.getLogger(ClientConnection.class.getName());

    private final Map<Integer, Request> requests = new ConcurrentHashMap<>();
    private final List<Request> failedRequests = new ArrayList<>();

    private ResponseProcessor responseProcessor;

    private int current = 0;
    private int max = 1000;
    private int failed = 0;

    public ClientConnection() {
        this(null);
    }

    public ClientConnection(final Socket paramSocket) {
        this(paramSocket, new AcceptAllResponseProcessorFactory());
    }

    public ClientConnection(final Socket paramSocket, final ResponseProcessorFactory paramResponseProcessorFactory) {
        super(paramSocket);
        setLogger(logger);
        setResponseProcessorFactory(paramResponseProcessorFactory);
    }

    public ClientConnection(final String paramHost, final int paramPort) {
        this(paramHost, paramPort, new AcceptAllResponseProcessorFactory());
    }

    public ClientConnection(final String paramHost, final int paramPort, final ResponseProcessorFactory paramResponseProcessorFactory) {
        super(paramHost, paramPort);
        setLogger(logger);
        setResponseProcessorFactory(paramResponseProcessorFactory);
    }

    @Override
    public final void manageConnection() {
        receive();
        for (int i : requests.keySet()) {
            final Request request = requests.get(i);
            transmit(request.getRequest());
            String response = receive();
            if (null == response) {
                request.failed(true);
                failedRequests.add(request);
                requests.remove(i);
                failed++;
            } else if (response.isEmpty()) {
                if (request.fails()) {
                    failedRequests.add(request);
                    requests.remove(i);
                    failed++;
                }
            } else {
                request.setResponse(response);
                requests.remove(i);
            }
        }
    }

    public final synchronized String getResponse(final String paramRequest) {
        transmit(paramRequest);
        return receive();
    }

    public final synchronized Request send(final String paramData) {
        final Request request = new Request(paramData);
        if (current >= max) {
            current = 0;
        }
        requests.put(current++, request);
        return request;
    }

    public final void setResponseProcessorFactory(final ResponseProcessorFactory paramResponseProcessorFactory) {
        if (null != paramResponseProcessorFactory) {
            responseProcessor = paramResponseProcessorFactory.create();
        }
    }

    public final boolean hasRequestsLeft() {
        return !requests.isEmpty();
    }

    public final int getFailedRequestCount() {
        return failed;
    }

    public final List<Request> getFailedRequests() {
        return failedRequests;
    }

    public final void closeConnection() {
        connected = false;
    }
}
