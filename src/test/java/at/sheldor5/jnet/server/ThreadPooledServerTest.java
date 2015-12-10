package at.sheldor5.jnet.server;

import at.sheldor5.jnet.TestUtils;
import at.sheldor5.jnet.requestprocessors.EchoRequestProcessorFactory;
import at.sheldor5.jnet.requestprocessors.RequestProcessorFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class ThreadPooledServerTest {

    private static final Logger LOGGER = LogManager.getLogger(ThreadPooledServerTest.class.getName());

    private final RequestProcessorFactory requestProcessorFactory = new EchoRequestProcessorFactory();

    private final String host = "localhost";
    private final int port = 8080;

    private ThreadPooledServer server;

    @Before
    public void prepareTest() {
        TestUtils.printTestBanner("T E S T I N G   T H R E A D   P O O L E D   S E R V E R   -   E C H O   -   R P");
        Configurator.setRootLevel(Level.DEBUG);
        try {
            server = new ThreadPooledServer(port, requestProcessorFactory);
            server.start();
        } catch (final IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testThreadPooledServer() {
        final Responses responses = new Responses(host, port);
        final Map<String, String> map = responses.getResponses(TestUtils.ECHO_REQUESTS);
        TestUtils.printCommunication(map, LOGGER);
        System.out.println(responses.getAverageResponseTime() + "ms");
    }

    @After
    public void shutdownTest() {
        server.stopServer();
        try {
            server.join();
        } catch (final InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }
}
