package at.sheldor5.jnet.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Mihcael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class PermaConnection extends Connection {

    public PermaConnection(Socket paramSocket) {
        super(paramSocket);
    }

    public PermaConnection(String paramHost, int paramPort) throws IOException {
        super(paramHost, paramPort);
    }

    @Override
    public boolean onTimeOut(SocketTimeoutException e) {
        return false;
    }
}
