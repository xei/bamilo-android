package com.mobile.newFramework.rest.errors;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigBaseException extends Exception {

    private JumiaError error;

    public AigBaseException(JumiaError error) {
        super();
        this.error = error;
    }

    public JumiaError getError() {
        return error;
    }

    public void setError(JumiaError error) {
        this.error = error;
    }
}
