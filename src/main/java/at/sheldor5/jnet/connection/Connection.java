package at.sheldor5.jnet.connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Application protocol:
 *
 * optional:
 *
 * /!\MD5HASH/!\
 * {MD5 hash of the the data set}
 *
 *
 * arbitrary lines of Data representing the Data-Set:
 *
 * {data set ...}
 * {...}
 *
 *
 * required:
 *
 * /!\EODS/!\
 *
 *
 * Automated keep alive mechanism:
 *
 * Sending keep-alive request on timeout:
 *
 * /!\ALIVE?/!\
 * /!\EODS/!\
 *
 * Responding on keep-alive request with:
 *
 * /!\ALIVE!/!\
 * /!\EODS/!\
 */

/**
 * Created by Michael Palata <a href="https://github.com/Sheldor5">@github.com/Sheldor5</a> on 09.12.2015.
 *
 * Class to handle basic read/write operations on {@link Socket}s.
 * The socket and his Input- and OutputStream is kept open until {@link #close()} is called or a time out occurs.
 *
 * Started as thread the {@link Thread}'s {@link #run()} method will call {@link #manageConnection()} as long as the
 * socket's connection is open. Inside the {@link #manageConnection()} method you should implement your r/w logic
 * by using {@link #receive()} to get the next incoming Data-Set and {@link #transmit(String)} to send back a Data-Set.
 */
public abstract class Connection extends Thread {

    /** Logger */
    private Logger logger = LogManager.getLogger(Connection.class.getName());

    /** End-Of-Data-Set pattern */
    private static String EODS = "/!\\EODS/!\\";

    /** End-Of-Data-Set pattern including leading and trailing new line character */
    private static String WR_EOR = "\n" + EODS + "\n";

    /** Keep-Alive request pattern */
    private static String ALV_REQ = "/!\\ALIVE?/!\\";

    /** Keep-Alive response pattern */
    private static String ALV_RES = "/!\\ALIVE!/!\\";

    /** MD5-Hash pattern */
    private static String HASH = "/!\\MD5HASH/!\\";

    /** MD5-Hash pattern including trailing new line character */
    private static String WR_HASH = HASH + "\n";

    private boolean alive = true;

    private Socket socket = null;

    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    private final StringBuilder dataSet = new StringBuilder();

    private final Hash md5 = new Hash();

    private int port = 0;
    private String host = "";

    private boolean isDataSet = true;

    protected volatile boolean connected = false;

    /** Use existing socket.
     *
     * @param paramSocket The existing socket to use.
     */
    protected Connection(final Socket paramSocket) {
        connect(paramSocket);
    }

    /** Use new socket by establishing a connection to the given host and port.
     *
     * @param paramHost The host to connect to.
     * @param paramPort The port on which the application on the host is listening.
     */
    protected Connection(final String paramHost, final int paramPort) {
        try {
            socket = new Socket(paramHost, paramPort);
            connect(socket);
        } catch (final IOException e) {
            logger.error("Error connecting socket: {}", e.getMessage());
            connected = false;
        }
    }

    /**
     * Reads the next incoming Data-Set from the underlying socket.
     *
     * @return The next incoming Data-Set.
     */
    protected final synchronized String receive() {
        String data = null;
        if (connected) {
            try {
                String in;
                String receivedHash = null;
                dataSet.setLength(0);
                while (connected) {
                    // get next line of the data set
                    in = reader.readLine();
                    logger.debug("<<< {}", in);
                    if (null == in) {
                        // connection closed from other side
                        return null;
                    } else if (EODS.equals(in)) {
                        // End-Of-Data-Set pattern detected, break loop to return the data set
                        logger.debug("End-Of-Data-Set detected!");
                        break;
                    } else if (HASH.equals(in)) {
                        // MD5-Hash pattern detected, the next line of the data set is the MD5 hash of the data set
                        logger.debug("MD5-Hash detected!");
                        final String tmp = reader.readLine();
                        logger.debug("<<< {}", tmp);
                        // check if hash is a valid MD5 pattern
                        if (null == tmp) {
                            logger.error("Error completing Data-Set: disconnected");
                        } else if (tmp.matches("^[a-f0-9]{32}$")) {
                            receivedHash = tmp;
                            logger.debug("Received MD5-Hash of this Data-Set: {}", receivedHash);
                        } else {
                            logger.error("Error reading MD5-Hash of Data-Set: {}", tmp);
                        }
                    } else if (ALV_REQ.equals(in)) {
                        // Keep-Alive-Request pattern detected, send back the Keep-Alive-Response pattern
                        logger.debug("Keep-Alive-Request detected!");
                        final String tmp = reader.readLine();
                        logger.debug("<<< {}", tmp);
                        if (tmp == null) {
                            logger.error("Error completing Keep-Alive-Request: disconnected");
                            return null;
                        } else if (EODS.equals(tmp)) {
                            transmit(ALV_RES);
                        } else {
                            transmit(ALV_RES);
                            logger.warn("Missing End-Of-Data-Set pattern of Keep-Alive-Request, was \"{}\", Keep-Alive-Response will be sent back anyway", tmp);
                        }
                    } else if (ALV_RES.equals(in)) {
                        // Keep-Alive-Response pattern detected, resetting alive flag.
                        logger.debug("Keep-Alive-Response detected!");
                        alive = true;
                        final String tmp = reader.readLine();
                        logger.debug("<<< {}", tmp);
                        if (tmp == null) {
                            logger.error("Error completing Keep-Alive-Response: disconnected");
                            return null;
                        } else if (!EODS.equals(tmp)) {
                            logger.warn("Missing End-Of-Data-Set pattern of Keep-Alive-Response, was \"{}\"", tmp);
                        }
                    } else {
                        // add line to the Data-Set builder
                        dataSet.append(in).append("\n");
                    }
                }
                // get the full Data-Set
                data = dataSet.toString().trim();
                // check MD5-Hash of the Data-Set if MD5-Hash was included in Data-Set
                if (null != receivedHash) {
                    final String calculatedHash = md5.getMD5(data);
                    if (!receivedHash.equals(calculatedHash)) {
                        logger.error("Error in transmission, hashes doesn't match: {} != {}", receivedHash, calculatedHash);
                    }
                    logger.debug("Calculated MD5-Hash of this Data-Set: {}", calculatedHash);
                }
            } catch (final SocketTimeoutException e) {
                // Send Keep-Alive-Request pattern on read time out
                if (alive) {
                    alive = false;
                    transmit(ALV_REQ);
                    data = receive();
                } else {
                    logger.error("Connection timed out, closing connection");
                    connected = false;
                    return null;
                }
            } catch (final IOException e) {
                logger.error("Error receiving data: {}", e.getMessage());
            }
        } else {
            logger.error("Error receiving data: not connected");
            return null;
        }
        return data;
    }

    /**
     * Writes a Data-Set to the underlying socket.
     *
     * @param paramDataSet The Data-Set to send.
     */
    protected final synchronized void transmit(final String paramDataSet) {
        if (connected) {
            if (null == paramDataSet) {
                logger.error("Error sending data: data is null");
            } else if (paramDataSet.contains(WR_EOR)) {
                logger.error("Error sending data: data contains illegal string \"\\n{}\\n\"", EODS);
            }  else if (paramDataSet.contains(WR_HASH)) {
                logger.error("Error sending data: data contains illegal string \"\\n{}\\n\"", HASH);
            } else {
                final String hash = md5.getMD5(paramDataSet);
                try {
                    if (hash != null && alive) {
                        writer.write(WR_HASH);
                        writer.write(hash);
                        writer.write("\n");
                        logger.debug(">>> {}", HASH);
                        logger.debug(">>> {}", hash);
                    }
                    writer.write(paramDataSet);
                    writer.write(WR_EOR);
                logger.debug(">>> {}", paramDataSet);
                logger.debug(">>> {}", EODS);
                    writer.flush();
                } catch (final IOException e) {
                    logger.error("Error sending data: {}", e.getMessage());
                }
            }
        } else {
            logger.error("Error sending data: not connected");
        }
    }

    /**
     * Closes the Input- and OutputStream and the socket.
     * Once this method is called, whether there are errors or not, this {@link Connection} object
     * can not be used until {@link #connect(Socket)} or {@link #connect(String host, int port)}
     * is called and a new connection was successfully established.
     */
    private final void close() {
        if (connected) {
            logger.debug("Closing connection");
            connected = false;
            try {
                if (null != writer) {
                    writer.flush();
                    writer.close();
                    writer = null;
                } else {
                    logger.warn("Error closing OutputStream: null");
                }
            } catch (final IOException e) {
                logger.error("Error closing OutputStream: {}", e.getMessage());
            }
            try {
                if (null != reader) {
                    reader.close();
                    reader = null;
                } else {
                    logger.warn("Error closing InputStream: null");
                }
            } catch (final IOException e) {
                logger.error("Error closing InputStream: {}", e.getMessage());
            }
            if (null != socket && !socket.isClosed()) {
                try {
                    socket.close();
                    socket = null;
                } catch (final IOException e) {
                    logger.error("Error closing Socket: {}", e.getMessage());
                }
            } else {
                logger.warn("Error closing Socket: null or already closed");
            }
        }
    }

    /**
     * The {@link #run()} method from the extended {@link Thread} class.
     * If this {@link Connection} is started as a thread, it will permanently call {@link #manageConnection()}
     * in a loop as long as the connection is open.
     */
    public final void run() {
        if (null != socket) {
            connected = true;
        }
        logger.debug("Connection opened");
        while (connected) {
            manageConnection();
        }
        close();
        logger.debug("Connection closed");
    }

    /**
     * Abstract methode where you should implement your r/w logic by using {@link #receive()} to
     * get the next incoming Data-Set and {@link #transmit(String)} to send back a Data-Set.
     */
    public abstract void manageConnection();

    public final boolean isConnected() {
        return connected;
    }

    public final void setTimeOut(final int paramTimeOutMillis) {
        if (connected) {
            try {
                socket.setSoTimeout(paramTimeOutMillis);
            } catch (final SocketException e) {
                logger.error("Error setting socket time out: {}", e.getMessage());
            }
        }
    }

    /**
     * Establish a connection by using an existing socket.
     *
     * @param paramSocket The already existing socket.
     */
    public final void connect(final Socket paramSocket) {
        if (connected) {
            close();
        }
        if (null != paramSocket) {
            socket = paramSocket;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                connected = true;
            } catch (final IOException e) {
                connected = false;
                logger.error("Error opening Socket: {}", e.getMessage());
            }
        }
    }

    /**
     * Establish a connection with a new socket by the given host and port.
     *
     * @param paramHost The host to connect to.
     * @param paramPort The port on which the application on the host is listening.
     */
    public final void connect(final String paramHost, final int paramPort) {
        if (connected) {
            close();
        }
        try {
            connect(new Socket(paramHost, paramPort));
        } catch (final IOException e) {
            logger.error("Error opening Socket: {}", e.getMessage());
        }
    }

    protected void setLogger(final Logger paramLogger) {
        if (null != paramLogger) {
            logger = paramLogger;
        }
    }
}
