package com.mobile.service.rest.errors;

/**
 * Base
 * @author pcarvalho
 */
public class AigBaseException extends Exception {

    private AigError error;

    public AigBaseException(AigError error) {
        super();
        this.error = error;
    }

    public AigError getError() {
        return error;
    }

    public void setError(AigError error) {
        this.error = error;
    }
}
