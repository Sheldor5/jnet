package at.sheldor5.jnet.processors.responses;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 13.12.2015.
 */
public class AcceptAllResponseProcessorFactory implements ResponseProcessorFactory {
    @Override
    public ResponseProcessor create() {
        return new AcceptAllResponseProcessor();
    }
}
