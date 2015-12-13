package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.ServerConnection;
import at.sheldor5.jnet.processors.requests.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 */
public class ThreadPooledServer extends Server {

    private final Logger logger = LogManager.getLogger(ThreadPooledServer.class.getName());

    public static final int MAX_THREADS = 100;
    public static final int DEFAULT_THREADS = 10;

    private final ExecutorService threadPool;

    public ThreadPooledServer(final int paramPort, final int paramThreads, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException{
        super(paramPort, paramRequestProcessorFactory);
        if (paramThreads > 0 && paramThreads <= MAX_THREADS) {
            threadPool = Executors.newFixedThreadPool(paramThreads);
        } else {
            logger.warn("Illegal number of request processor threads \"{}\", using default amount of request processor threads of \"\"", paramThreads, DEFAULT_THREADS);
            threadPool = Executors.newFixedThreadPool(DEFAULT_THREADS);
        }
    }

    public ThreadPooledServer(final int paramPort, final RequestProcessorFactory paramRequestProcessorFactory) throws IOException {
        this(paramPort, DEFAULT_THREADS, paramRequestProcessorFactory);
    }

    @Override
    public void run() {
        logger.info("Server started");
        while (running) {
            while (running) {
                try {
                    final Socket client = serverSocket.accept();
                    if (connectionTimeOutIsSet) {
                        client.setSoTimeout(connectionTimeOut);
                    }
                    threadPool.execute(new ServerConnection(client, requestProcessorFactory));
                } catch (final IOException e) {
                    if (running) {
                        logger.error("Error handling request: {}", e.getMessage());
                    }
                }
            }
        }
        logger.info("Server stopped");
    }

}
