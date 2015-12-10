package at.sheldor5.jnet.connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 *
 * Class to handle basic read/write operations on sockets.
 * The socket and his Input- and OutputStream is kept open until close() is called.
 */
public abstract class Connection {

    private static final Logger LOGGER = LogManager.getLogger(Connection.class.getName());

    public static final String EOM = "/EOM";

    private static final String WR_EOM = "\n" + EOM + "\n";

    public Socket socket = null;

    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    private final StringBuilder stringBuilder = new StringBuilder();
    private String lastInput = "";
    private String lastOutput = "";
    private String in = "";
    private String data = null;

    private volatile boolean connected = false;

    protected Connection(final Socket paramSocket) {
        connect(paramSocket);
    }

    protected Connection(final String paramHost, final int paramPort) throws IOException {
        this(new Socket(paramHost, paramPort));
    }

    /**
     * Handle read time out.
     *
     * @param e the thrown SocketTimeoutException.
     * @return if the connection should be closed or not.
     */
    protected abstract boolean onTimeOut(final SocketTimeoutException e);

    protected final void transmit(final String paramMessage) {
        if (connected) {
            if (null == paramMessage) {
                LOGGER.error("Error sending data: data is null");
            }
            else if (paramMessage.contains(WR_EOM)) {
                LOGGER.error("Error sending data: data contains illegal string \"\\n{}\\n\"", EOM);
            } else {
                try {
                    writer.write(paramMessage);
                    writer.write(WR_EOM);
                    writer.flush();
                    lastOutput = paramMessage;
                } catch (final IOException e) {
                    LOGGER.error("Error sending data: {}", e.getMessage());
                }
            }
        } else {
            LOGGER.error("Error sending data: not connected");
        }
    }

    protected final String receive() {
        if (connected) {
            try {
                stringBuilder.setLength(0);
                while (true) {
                    in = reader.readLine();
                    if (null == in || EOM.equals(in)) {
                        break;
                    }
                    stringBuilder.append(in).append("\n");
                }
                data = stringBuilder.toString().trim();
                lastInput = data;
            } catch (final SocketTimeoutException e) {
                data = null;
                if (onTimeOut(e)) {
                    close();
                }
            } catch (final IOException e) {
                LOGGER.error("Error receiving data: {}", e.getMessage());
            }
        } else {
            LOGGER.error("Error receiving data: not connected");
        }
        return data;
    }

    public final String getLastInput() {
        return lastInput;
    }

    public final String getLastOutput() {
        return lastOutput;
    }

    public final void connect(final Socket paramSocket) {
        if (connected) {
            connected = false;
            close();
        }
        if (null != paramSocket) {
            socket = paramSocket;
            //LOGGER.debug("Reconnecting to {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;
            } catch (final IOException e) {
                LOGGER.error("Error opening Socket: {}", e.getMessage());
                connected = false;
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
            LOGGER.error("Error opening Socket: {}", e.getMessage());
        }
    }

    protected final void close() {
        //LOGGER.debug("Closing connection ...");
        connected = false;
        try {
            if (null != writer) {
                writer.close();
            }
        } catch (final IOException e) {
            LOGGER.error("Error closing OutputStream: {}", e.getMessage());
        }

        try {
            if (null != reader) {
                reader.close();
            }
        } catch (final IOException e) {
            LOGGER.error("Error closing InputStream: {}", e.getMessage());
        }

        if (null != socket && !socket.isClosed()) {
            try {
                socket.close();
            } catch (final IOException e) {
                LOGGER.error("Error closing Socket: {}", e.getMessage());
            }
        }
        //LOGGER.debug("Connection closed");
    }

    public final boolean isConnected() {
        return connected;
    }

    public final void setTimeOut(final int paramTimeOutMillis) {
        if (connected && null != socket) {
            try {
                socket.setSoTimeout(paramTimeOutMillis);
            } catch (final SocketException e) {
                LOGGER.error("Error setting socket time out: {}", e.getMessage());
            }
        }
    }
}
