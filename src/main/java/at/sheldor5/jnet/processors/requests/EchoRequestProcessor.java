package at.sheldor5.jnet.processors.requests;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class EchoRequestProcessor extends RequestProcessor {
    @Override
    public String process(final String paramRequest) {
        return paramRequest;
    }
}
