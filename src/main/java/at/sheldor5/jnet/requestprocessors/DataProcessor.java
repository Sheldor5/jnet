package at.sheldor5.jnet.requestprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 09.12.2015.
 */
public abstract class DataProcessor {

    protected final Logger logger = LogManager.getLogger(DataProcessor.class.getName());

    /**
     * Processes the request and returns the response for this request.
     *
     * @param paramRequest The request to process.
     * @return The response to this request.
     */
    public abstract String process(final String paramRequest);

}
