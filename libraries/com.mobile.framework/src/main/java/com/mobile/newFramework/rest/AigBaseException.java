package com.mobile.newFramework.rest;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class AigBaseException extends Exception {

    private JumiaError error;

//    public AigBaseException(String msg, JumiaError error) {
//        super(msg);
//        this.error = error;
//    }

    public AigBaseException(JumiaError error) {
        super();
        this.error = error;
    }

//    public AigBaseException(String msg) {
//        super(msg);
//    }
//
//    public AigBaseException() {
//        super();
//    }

    public JumiaError getError() {
        return error;
    }

    public void setError(JumiaError error) {
        this.error = error;
    }
}
