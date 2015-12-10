package at.sheldor5.jnet.server;

import java.io.IOException;

/**
 * Created by Mihcael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class SingleConnectionServer extends Server {

    /**
     * Constructor to create the ServerSocket on the desired port.
     *
     * @param paramPort The port on which this server should listen. Port 0 means automatically use the next free port.
     * @throws IOException
     */
    public SingleConnectionServer(int paramPort) throws IOException {
        super(paramPort);
    }

    @Override
    public void run() {

    }
}
