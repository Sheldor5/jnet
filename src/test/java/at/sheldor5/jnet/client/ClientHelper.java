package at.sheldor5.jnet.client;

import at.sheldor5.jnet.connection.ClientConnection;
import at.sheldor5.jnet.connection.Request;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 12.12.2015.
 */
public class ClientHelper extends Thread {

    private ClientConnection connection;

    private final String[] strings;
    private final List<Request> requests = new ArrayList<>();

    public ClientHelper(final String paramHost, final int paramPort, final String[] paramRequests) {
        connection = new ClientConnection(paramHost, paramPort);
        strings = paramRequests;
        connection.start();
    }

    public final void testRequests(final String[] paramRequests) {
    }

    public void run() {
        for (final String request : strings) {
            requests.add(connection.send(request));
        }
        int c = 0;
        do {
            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } while (connection.hasRequestsLeft() && c++ < 1000);
        connection.closeConnection();
        Assert.assertEquals(0, connection.getFailedRequestCount());
        for (final Request request : requests) {
            Assert.assertEquals(request.getRequest(), request.getResponse());
        }
    }
}
