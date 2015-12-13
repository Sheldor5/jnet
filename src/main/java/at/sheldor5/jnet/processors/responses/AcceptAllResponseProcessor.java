package at.sheldor5.jnet.processors.responses;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 13.12.2015.
 */
public class AcceptAllResponseProcessor extends ResponseProcessor {
    @Override
    public boolean process(String paramResponse) {
        return true;
    }
}
