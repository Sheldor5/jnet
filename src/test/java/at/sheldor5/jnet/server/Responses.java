package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.ClientConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class Responses {

    private final String host;
    private final int port;

    private final Map<String, String> rr = new HashMap<>();

    private long start = 0L;
    private double[] time;
    private int err = 0;

    public Responses(final String paramHost, final int paramPort) {
        host = paramHost;
        port = paramPort;
    }

    public final Map<String, String> getResponses(final String[] paramRequests) {
        time = new double[paramRequests.length];
        err = 0;
        rr.clear();
        ClientConnection connection;

        try {
            connection = new ClientConnection();
        } catch (final Exception e) {
            return null;
        }

        for (int i = 0; i < paramRequests.length; i++) {
            try {
                final String request = paramRequests[i];
                start = System.nanoTime();
                connection.connect(host, port);
                final String response = connection.getResponse(request);
                rr.put(request, response);
                time[i] = (System.nanoTime() - start) / 1000000;
            } catch (final Exception e) {
                err++;
            }
        }
        return rr;
    }

    public double getAverageResponseTime() {
        double summ = 0.0;
        final int r = time.length - err;
        for (double t : time) {
            summ += t;
        }
        return (summ / time.length);
    }

}
