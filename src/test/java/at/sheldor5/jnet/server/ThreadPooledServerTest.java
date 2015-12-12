package at.sheldor5.jnet.server;

import at.sheldor5.jnet.TestUtils;
import at.sheldor5.jnet.client.Client;
import at.sheldor5.jnet.client.ClientHelper;
import at.sheldor5.jnet.connection.ClientConnection;
import at.sheldor5.jnet.connection.Request;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class ThreadPooledServerTest {

    private final String host = "localhost";
    private final int port = 1337;

    private final int maxClients = 5;

    private final RequestProcessorFactory requestProcessorFactory = new EchoRequestProcessorFactory();

    private final List<ClientHelper> clients = new ArrayList<>();

    private ThreadPooledServer server;

    @Before
    public void prepareTest() {
        Configurator.setRootLevel(Level.DEBUG);
        try {
            server = new ThreadPooledServer(port, requestProcessorFactory);
            server.start();
        } catch (final IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testThreadPooledEchoServer() {
        TestUtils.printTestBanner("T E S T I N G   T H R E A D   P O O L E D   S E R V E R   -   E C H O");
        for (int i = 0; i < maxClients; i++) {
            final ClientHelper clientHelper = new ClientHelper(host, port, TestUtils.ECHO_REQUESTS);
            clientHelper.start();
            clients.add(clientHelper);
        }
        for (final ClientHelper client : clients) {
            try {
                client.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
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
