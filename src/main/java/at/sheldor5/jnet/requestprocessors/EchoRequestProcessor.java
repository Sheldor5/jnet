package at.sheldor5.jnet.requestprocessors;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class EchoRequestProcessor extends DataProcessor {
    @Override
    public String process(final String paramRequest) {
        return paramRequest;
    }
}
