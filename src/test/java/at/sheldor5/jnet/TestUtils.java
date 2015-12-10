package at.sheldor5.jnet;

/**
 * Created by Mihcael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class TestUtils {

    private static final String LINE = "############################################################";

    public static void waitMillis(final long paramMillis) {
        try {
            Thread.sleep(paramMillis);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitSeconds(final int paramSeconds) {
        try {
            Thread.sleep(1000L * (long) paramSeconds);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printTestBanner(final String paramTestTitle) {
        System.out.println();
        System.out.println(LINE);
        System.out.println();
        System.out.println(paramTestTitle);
        System.out.println();
        System.out.println(LINE);
        System.out.println();
    }
}
