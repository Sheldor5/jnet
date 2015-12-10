package at.sheldor5.jnet.requestprocessors;

import java.net.Socket;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 10.12.2015.
 */
public class EchoRequestProcessorFactory implements RequestProcessorFactory {

    @Override
    public RequestProcessor create(final Socket paramSocket) {
        return new EchoRequestProcessor(paramSocket);
    }
}
