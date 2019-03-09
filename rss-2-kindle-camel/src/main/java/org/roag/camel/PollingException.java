package org.roag.camel;

/**
 * Created by eurohlam on 2/01/19.
 */
public class PollingException extends Exception {

    public PollingException(String message) {
        super(message);
    }

    public PollingException(String message, Throwable cause) {
        super(message, cause);
    }
}
