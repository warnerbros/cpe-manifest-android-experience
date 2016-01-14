package net.flixster.android.net;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Exception to represent HTTP status 401 Unauthorized
 * 
 * @see {@link HttpURLConnection.HTTP_UNAUTHORIZED}
 */
public class HttpUnauthorizedException extends IOException {
    private static final long serialVersionUID = 2304460981362453296L;

    public HttpUnauthorizedException() {
    }

    public HttpUnauthorizedException(String responseMessage) {
        super(responseMessage);
    }

    public HttpUnauthorizedException(String responseMessage, Throwable cause) {
        super(responseMessage + cause.getMessage());
    }

    /** @return {@link HttpURLConnection.HTTP_UNAUTHORIZED} */
    public int getResponseCode() {
        return HttpURLConnection.HTTP_UNAUTHORIZED;
    }

    /** @return Response message */
    public String getResponseMessage() {
        return getMessage();
    }
}
