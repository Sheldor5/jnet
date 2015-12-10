package at.sheldor5.jnet.requestprocessors;

import at.sheldor5.jnet.connection.ServerConnection;

import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class EchoRequestProcessor extends RequestProcessor {

    private final ServerConnection connection;

    public EchoRequestProcessor(final Socket paramSocket) {
        super(paramSocket);
        connection = new ServerConnection(paramSocket);
    }

    @Override
    public void run() {
        connection.sendResponse(connection.getRequest());
    }
}
