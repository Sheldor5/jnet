package at.sheldor5.jnet.requestprocessors;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 12.12.2015.
 */
public class NullRequestProcessorFactory implements RequestProcessorFactory {
    @Override
    public DataProcessor create() {
        return new NullRequestProcessor();
    }
}
