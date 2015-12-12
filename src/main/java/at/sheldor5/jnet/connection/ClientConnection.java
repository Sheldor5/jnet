package at.sheldor5.jnet.connection;

import at.sheldor5.jnet.requestprocessors.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 *
 * Simple connection object for clients.
 * Receives the response of the desired request and closes the socket.
 */
public class ClientConnection extends Connection {

    /** Logger */
    private static final Logger logger = LogManager.getLogger(ClientConnection.class.getName());

    private final Map<Integer, Request> requests = new ConcurrentHashMap<>();
    private final List<Request> failedRequests = new ArrayList<>();

    private String successfulResponse = "HTTP/1.1 200 OK";

    private int current = 0;
    private int failed = 0;

    public ClientConnection() {
        super(null);
    }

    public ClientConnection(Socket paramSocket) {
        super(paramSocket);
    }

    public ClientConnection(String paramHost, int paramPort) {
        super(paramHost, paramPort);
    }

    @Override
    public final void manageConnection() {
        while (!requests.isEmpty()) {
            logger.debug("Client started");
            for (int i : requests.keySet()) {
                final Request request = requests.get(i);
                transmit(request.getRequest());
                final String response = receive();
                if (null != response) {
                    request.setResponse(response);
                    requests.remove(i);
                } else if (request.fails()) {
                    failedRequests.add(request);
                    requests.remove(i);
                    failed++;
                }
            }
        }
    }

    public final String getResponse(final String paramRequest) {
        transmit(paramRequest);
        return receive();
    }

    public final synchronized Request send(final String paramData) {
        final Request request = new Request(paramData);
        if (current == Integer.MAX_VALUE - 1) {
            current = 0;
        }
        requests.put(current++, request);
        return request;
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
        close();
    }
}
