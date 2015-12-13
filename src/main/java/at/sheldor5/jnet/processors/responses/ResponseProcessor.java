package at.sheldor5.jnet.processors.responses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 13.12.2015.
 */
public abstract class ResponseProcessor {

    protected final Logger logger = LogManager.getLogger(ResponseProcessor.class.getName());

    /**
     * Processes the response and returns true if response is valid, otherwise false.
     *
     * @param paramResponse The response to process.
     * @return Weather the response is valid or not.
     */
    public abstract boolean process(final String paramResponse);
}
