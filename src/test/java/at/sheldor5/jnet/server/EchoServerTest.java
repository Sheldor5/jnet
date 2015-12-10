package at.sheldor5.jnet.server;

import at.sheldor5.jnet.connection.ClientConnection;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import at.sheldor5.jnet.TestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;

/**
 * Created by Mihcael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class EchoServerTest {

    EchoServer echoServer = EchoServer.getInstance(1337);

    static String HOST = "localhost";
    static int PORT = 1337;

    String[] s = {"Hi", "How are you?", "Nice to meet you!", "Bye!"};

    long start = 0L;
    float time = 0F;

    @Before
    public void startEchoServer() {
        TestUtils.printTestBanner("T E S T I N G   E C H O   S E R V E R");
        Configurator.setRootLevel(Level.DEBUG);
        echoServer.start();
    }

    @Test
    public void testEchoServer() {
        try {
            final ClientConnection connection = new ClientConnection();
            for (final String request : s) {
                TestUtils.waitMillis(500);
                System.out.println();
                System.out.println(String.format("client> \"%s\"", request));

                start = System.nanoTime();

                connection.connect(HOST, PORT);
                String response = connection.getResponse(request);

                time = (System.nanoTime() - start) / 1000000F;

                System.out.println(String.format("server> \"%s\" (%.3fms)", response, time));
                Assert.assertEquals(response, request);
            }
            System.out.println();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @After
    public void stopEchoServer() {
        echoServer.stopServer();
        try {
            echoServer.join();
        } catch (final InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
    }
}
