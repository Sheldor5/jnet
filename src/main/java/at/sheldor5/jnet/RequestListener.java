package at.sheldor5.jnet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */
public abstract class RequestListener extends Thread {

    public static final Logger LOGGER = LogManager.getLogger(RequestListener.class.getName());
    public static final int MAX_THREADS = 100;

    private final ExecutorService threadPool;
    private boolean running = false;

    RequestListener(final int paramThreads) {
        if (paramThreads > 0 && paramThreads <= 100) {
            threadPool = Executors.newFixedThreadPool(paramThreads);
        }
    }

    public final void startListener() {
        LOGGER.debug("Starting request listener ...");
        running = true;
        start();
    }

    public final void stopListener() {
        LOGGER.debug("Stopping request listener ...");
        running = false;
    }

    public final boolean isRunning() {
        return running;
    }
}
