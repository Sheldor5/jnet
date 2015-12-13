package at.sheldor5.jnet.processors.requests;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 12.12.2015.
 */
public class EmptyRequestProcessorFactory implements RequestProcessorFactory {
    @Override
    public RequestProcessor create() {
        return new EmptyRequestProcessor();
    }
}
