package at.sheldor5.jnet.server;

import at.sheldor5.jnet.client.ClientHelper;
import at.sheldor5.jnet.processors.requests.EchoRequestProcessorFactory;
import at.sheldor5.jnet.processors.requests.RequestProcessorFactory;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import at.sheldor5.jnet.TestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;

import java.io.IOException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class SingleThreadServerTest {

    private final String host = "localhost";
    private final int port = 8080;

    private final int clients = 5;

    private final RequestProcessorFactory requestProcessorFactory = new EchoRequestProcessorFactory();

    private SingleThreadServer server;

    @Before
    public void prepareTest() {
        Configurator.setRootLevel(Level.DEBUG);
        try {
            server = new SingleThreadServer(port, requestProcessorFactory);
            server.setConnectionTimeOut(1000);
            server.start();
        } catch (final IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testEchoServer() {
        TestUtils.printTestBanner("T E S T I N G   S I N G L E   T H R E A D   S E R V E R   -   E C H O");
        final ClientHelper clientHelper = new ClientHelper(host, port, TestUtils.ECHO_REQUESTS);
        clientHelper.setKeepOpen(true);
        clientHelper.setKeepOpenFor(10000);
        clientHelper.start();
        try {
            clientHelper.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
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
