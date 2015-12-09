package at.sheldor5.jnet.connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015
 */

/**
 * Class to handle basic read/write operations on sockets.
 * The socket and his Input- and OutputStream is kept open until close() is called.
 */
public abstract class Connection {

    public static final Logger LOGGER = LogManager.getLogger(Connection.class.getName());

    public static final String EOM = "/EOM";
    private static final String WR_EOM = "\n" + EOM + "\n";

    public Socket socket = null;
    protected BufferedWriter writer = null;
    protected BufferedReader reader = null;

    private final StringBuilder stringBuilder = new StringBuilder();
    private String lastInput = "";
    private String lastOutput = "";
    private String in = "";
    private String data = null;

    private boolean connected = false;
    private final boolean closeAfterRead;
    private final boolean closeAfterWrite;

    public Connection(final Socket paramSocket, final boolean paramCloseAfterRead, final boolean paramCloseAfterWrite) {
        this.closeAfterRead = paramCloseAfterRead;
        this.closeAfterWrite = paramCloseAfterWrite;
        connect(paramSocket);
    }

    public Connection(final String paramHost, final int paramPort, final boolean paramCloseAfterRead, final boolean paramCloseAfterWrite) throws IOException {
        this(new Socket(paramHost, paramPort), paramCloseAfterRead, paramCloseAfterWrite);
    }

    /**
     * Handle read time out.
     *
     * @param e the thrown SocketTimeoutException.
     * @return if the connection should be closed or not.
     */
    public abstract boolean onTimeOut(final SocketTimeoutException e);

    public final void transmit(final String paramMessage) {
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
                    if (closeAfterWrite) {
                        close();
                    }
                    lastOutput = paramMessage;
                } catch (final IOException e) {
                    LOGGER.error("Error sending data: {}", e.getMessage());
                }
            }
        } else {
            LOGGER.error("Error sending data: not connected");
        }
    }

    public final String receive() {
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
                if (closeAfterRead) {
                    close();
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
        socket = paramSocket;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (final Exception e) {
            LOGGER.error("Error opening Socket: {}", e.getMessage());
            connected = false;
        }
    }

    public final void close() {
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
