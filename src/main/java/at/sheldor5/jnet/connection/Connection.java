package at.sheldor5.jnet.connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.sheldor5.jnet.requestprocessors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Class to handle basic read/write operations on sockets.
 * The socket and his Input- and OutputStream is kept open until #close() is called or a time out occures.
 *
 * The default #run() of the extended Thread will permanently wait for a request and responses with
 * the result from the @RequestProcessor.
 *
 * By default this connection is best used for server side sockets.
 */
public abstract class Connection extends Thread {

    /** Logger */
    private static final Logger logger = LogManager.getLogger(Connection.class.getName());

    /** End Of Request pattern */
    public static String EOR = "/!\\EOR/!\\";

    /** End Of Request pattern including leading and trailing new line character */
    private static final String WR_EOR = "\n" + EOR + "\n";

    /** Alive Request pattern */
    public static final String ALV_REQ = "/!\\ALIVE?/!\\";

    /** Alive Response pattern */
    public static final String ALV_RES = "/!\\ALIVE!/!\\";

    public static String HASH = "/!\\MD5HASH/!\\";

    private static final String WR_HASH = HASH + "\n";

    private boolean alive = true;

    private Socket socket;

    private BufferedWriter writer = null;
    private BufferedReader reader = null;
    private boolean canHash = true;

    private final StringBuilder stringBuilder = new StringBuilder();

    private final Hash md5 = new Hash();

    protected int port = 0;
    protected String host = "";

    protected volatile boolean connected = false;

    protected Connection(final Socket paramSocket) {
        connect(paramSocket);
    }

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
     * Reads the next data set from the underlying socket.
     *
     * @return String representation of the incoming data, empty string if incoming data is no response of a specific request. Null if connection was closed or timed out.
     */
    protected final synchronized String receive() {
        String data = null;
        if (connected) {
            try {
                String in;
                String receivedHash = null;
                stringBuilder.setLength(0);
                while (connected) {
                    in = reader.readLine();
                    if (null == in) {
                        // connection closed from other side
                        return null;
                    } else if (EOR.equals(in)) {
                        // end of this request
                        logger.debug("<<< {}", in);
                        break;
                    } else if (HASH.equals(in)) {
                        // get hash
                        final String tmp = reader.readLine();
                        logger.debug("<<< {}", in);
                        if (null != tmp && tmp.matches("^[a-f0-9]{32}$")) {
                            receivedHash = tmp;
                            logger.debug("Received hash was: {}", receivedHash);
                        } else {
                            logger.error("Error reading MD5 hash of data: {}", tmp);
                        }
                    } else if (ALV_REQ.equals(in)) {
                        // requesting if connection is still alive
                        logger.debug("<<< {}", in);
                        transmit(ALV_RES);
                        break;
                    } else if (ALV_RES.equals(in)) {
                        // requesting if connection is still alive
                        logger.debug("<<< {}", in);
                        alive = true;
                        break;
                    } else {
                        // default
                        logger.debug("<<< {}", in);
                        stringBuilder.append(in).append("\n");
                    }
                }
                data = stringBuilder.toString().trim();
                if (null != receivedHash) {
                    final String calculatedHash = md5.getMD5(data);
                    if (!receivedHash.equals(calculatedHash)) {
                        logger.error("Error in transmission, hashes doesn't match: {} != {}", receivedHash, calculatedHash);
                    }
                    logger.debug("Calculated hash was: {}", calculatedHash);
                }
            } catch (final SocketTimeoutException e) {
                if (alive) {
                    alive = false;
                    transmit(ALV_REQ);
                    return "";
                } else {
                    logger.error("Connection timed out, closing connection");
                    close();
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

    protected final synchronized void transmit(final String paramMessage) {
        if (connected) {
            if (null == paramMessage) {
                logger.error("Error sending data: data is null");
            } else if (paramMessage.contains(WR_EOR)) {
                logger.error("Error sending data: data contains illegal string \"\\n{}\\n\"", EOR);
            }  else if (paramMessage.contains(WR_HASH)) {
                logger.error("Error sending data: data contains illegal string \"\\n{}\\n\"", HASH);
            } else {
                final String hash = md5.getMD5(paramMessage);
                try {
                    if (hash != null) {
                        writer.write(WR_HASH);
                        writer.write(hash);
                        writer.write("\n");
                        logger.debug(">>> {}", HASH);
                        logger.debug(">>> {}", hash);
                    }
                    writer.write(paramMessage);
                    writer.write(WR_EOR);
                logger.debug(">>> {}", paramMessage);
                logger.debug(">>> {}", EOR);
                    writer.flush();
                } catch (final IOException e) {
                    logger.error("Error sending data: {}", e.getMessage());
                }
            }
        } else {
            logger.error("Error sending data: not connected");
        }
    }

    protected final void close() {
        connected = false;
        try {
            if (null != writer) {
                writer.close();
            }
        } catch (final IOException e) {
            logger.error("Error closing OutputStream: {}", e.getMessage());
        }

        try {
            if (null != reader) {
                reader.close();
            }
        } catch (final IOException e) {
            logger.error("Error closing InputStream: {}", e.getMessage());
        }

        if (null != socket && !socket.isClosed()) {
            try {
                socket.close();
            } catch (final IOException e) {
                logger.error("Error closing Socket: {}", e.getMessage());
            }
        }
    }

    public final void run() {
        while (connected) {
            manageConnection();
        }
        close();
    }

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

    public final void connect(final Socket paramSocket) {
        if (connected) {
            connected = false;
            close();
        }
        if (null != paramSocket) {
            socket = paramSocket;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                host = paramSocket.getInetAddress().getHostAddress();
                port = paramSocket.getPort();
                connected = true;
            } catch (final IOException e) {
                connected = false;
                logger.error("Error opening Socket: {}", e.getMessage());
            }
        }
    }

    public final void connect(final String paramHost, final int paramPort) {
        if (connected) {
            connected = false;
            close();
        }
        try {
            connect(new Socket(paramHost, paramPort));
        } catch (final IOException e) {
            logger.error("Error opening Socket: {}", e.getMessage());
        }
    }

    public final void reconnect() {
        connect(host, port);
    }
}
