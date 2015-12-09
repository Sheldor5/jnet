package at.sheldor5.jnet.client;

import at.sheldor5.jnet.connection.BasicConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public abstract class Client extends BasicConnection {

    public static final Logger LOGGER = LogManager.getLogger(Client.class.getName());

    private final BasicConnection connection;

    public Client(final String paramHost, final int paramPort) throws IOException {
        super(paramHost, paramPort, true, false);
        connection = new BasicConnection(socket, true, false);
    }

    public final String getResponse(final String paramRequest) {
        return connection.
    }
}
