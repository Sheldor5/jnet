package at.sheldor5.jnet;

import org.junit.Assert;

import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
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

    public static synchronized void printCommunication(final Map<String, String> map, final Logger paramLogger) {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            final String request = entry.getKey();
            final String response = entry.getValue();
            paramLogger.info("client> {}", request);
            paramLogger.info("server> {}", response);
            System.out.println();
            Assert.assertEquals(request, response);
        }
    }

    public static double getAverageResponseTime(final double[] paramTimes) {
        double sum = 0.0;
        final int c = paramTimes.length;
        for (double t : paramTimes) {
            sum += t;
        }
        return (sum / c);
    }


    public static final String[] ECHO_REQUESTS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",/*
            "Video bietet eine leistungsstarke Möglichkeit zur Unterstützung Ihres Standpunkts. Wenn Sie auf \"Onlinevideo\" klicken, können Sie den Einbettungscode für das Video einfügen, das hinzugefügt werden soll. Sie können auch ein Stichwort eingeben, um online nach dem Videoclip zu suchen, der optimal zu Ihrem Dokument passt.",
            "Damit Ihr Dokument ein professionelles Aussehen erhält, stellt Word einander ergänzende Designs für Kopfzeile, Fußzeile, Deckblatt und Textfelder zur Verfügung. Beispielsweise können Sie ein passendes Deckblatt mit Kopfzeile und Randleiste hinzufügen. Klicken Sie auf \"Einfügen\", und wählen Sie dann die gewünschten Elemente aus den verschiedenen Katalogen aus.",
            "Designs und Formatvorlagen helfen auch dabei, die Elemente Ihres Dokuments aufeinander abzustimmen. Wenn Sie auf \"Design\" klicken und ein neues Design auswählen, ändern sich die Grafiken, Diagramme und SmartArt-Grafiken so, dass sie dem neuen Design entsprechen. Wenn Sie Formatvorlagen anwenden, ändern sich die Überschriften passend zum neuen Design.",
            "Sparen Sie Zeit in Word dank neuer Schaltflächen, die angezeigt werden, wo Sie sie benötigen. Zum Ändern der Weise, in der sich ein Bild in Ihr Dokument einfügt, klicken Sie auf das Bild. Dann wird eine Schaltfläche für Layoutoptionen neben dem Bild angezeigt Beim Arbeiten an einer Tabelle klicken Sie an die Position, an der Sie eine Zeile oder Spalte hinzufügen möchten, und klicken Sie dann auf das Pluszeichen.",
            "Auch das Lesen ist bequemer in der neuen Leseansicht. Sie können Teile des Dokuments reduzieren und sich auf den gewünschten Text konzentrieren. Wenn Sie vor dem Ende zu lesen aufhören müssen, merkt sich Word die Stelle, bis zu der Sie gelangt sind – sogar auf einem anderen Gerät."
             */};
}
