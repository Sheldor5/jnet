package at.sheldor5.jnet;

import at.sheldor5.jnet.connection.Hash;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 13.12.2015.
 */
public class HashTest {

    @Test
    public void testMD5() {
        final Hash hash = new Hash();
        String md5 = "";
        for (int i = 0; i < 20; i++) {
            md5 = hash.getMD5(md5);
            System.out.println(md5);
            Assert.assertTrue(md5.matches("^[a-f0-9]{32}$"));
        }
    }
}
