package at.sheldor5.jnet.connection;

/**
 * Created by Michael Palata [github.com/Sheldor5] on 12.12.2015.
 */
public class Request {

    private final String request;
    private String response = null;

    private int maxAttempt = 5;
    private int attempts = 0;
    private boolean failed = false;

    Request(final String paramRequest) {
        request = paramRequest;
    }

    public final String getRequest() {
        return request;
    }

    public final void setResponse(final String paramResponse) {
        response = paramResponse;
    }

    public final String getResponse() {
        return response;
    }

    public final void setAttempts(final int paramAttempts) {
        maxAttempt = paramAttempts;
    }

    public final boolean fails() {
        if (++attempts >= maxAttempt) {
            failed = true;
            return true;
        }
        return false;
    }

    public final boolean failed() {
        return failed;
    }

    public final void failed(final boolean paramFailed) {
        failed = paramFailed;
    }

}
