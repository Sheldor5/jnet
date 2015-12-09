package at.sheldor5.jnet.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public abstract class Server extends Thread {

    public static final Logger LOGGER = LogManager.getLogger(Server.class.getName());

    public final ServerSocket server;
    private boolean running = true;

    public Server(final int paramPort) throws IOException {
        server = new ServerSocket(paramPort);
    }

    public final boolean isRunning() {
        return running;
    }

    public final void stops() {
        running = false;
        interrupt();
        System.out.println("Serve stopped");
    }

}
