package at.sheldor5.jnet.requestprocessors;

import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 */
public abstract class RequestProcessor extends Thread {

    private final Socket socket;

    public RequestProcessor(final Socket paramSocket) {
        socket = paramSocket;
    }

    public abstract void run();

}
