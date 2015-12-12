package at.sheldor5.jnet.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 12.12.2015.
 */
public class Hash {

    private static final Logger logger = LogManager.getLogger(Connection.class.getName());
    private final StringBuffer stringBuffer = new StringBuffer();

    private boolean canHash = false;

    private MessageDigest md;

    public Hash() {
        try {
            md = MessageDigest.getInstance("MD5");
            canHash = true;
        } catch (final NoSuchAlgorithmException e) {
            canHash = false;
            logger.error("Error creating message digester: {}", e.getMessage());
        }
    }

    public final synchronized String getMD5(final String paramString) {
        if (canHash) {
            md.reset();
            md.update(paramString.getBytes());
            final byte byteData[] = md.digest();
            stringBuffer.setLength(0);
            for (int i = 0; i < byteData.length; i++) {
                stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuffer.toString();
        }
        return null;
    }
}
