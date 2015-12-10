package at.sheldor5.jnet.server;

import at.sheldor5.jnet.requestprocessors.EchoRequestProcessorFactory;
import at.sheldor5.jnet.requestprocessors.RequestProcessorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import at.sheldor5.jnet.TestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class SingleThreadServerTest {

    private static final Logger LOGGER = LogManager.getLogger(SingleThreadServerTest.class.getName());

    private final RequestProcessorFactory requestProcessorFactory = new EchoRequestProcessorFactory();

    private SingleThreadServer server;

    private final String host = "localhost";
    private final int port = 1337;


    @Before
    public void prepareTest() {
        TestUtils.printTestBanner("T E S T I N G   S I N G L E   T H R E A D   S E R V E R   -   E C H O   R P");
        Configurator.setRootLevel(Level.DEBUG);
        try {
            server = new SingleThreadServer(port, requestProcessorFactory);
            server.start();
        } catch (final IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testEchoServer() {
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
