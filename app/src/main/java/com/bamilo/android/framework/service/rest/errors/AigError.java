package com.bamilo.android.framework.service.rest.errors;

/**
 * AIG Error
 * @author pcarvalho
 */
public class AigError {

    private String mMessage;
    @ErrorCode.Code
    private int code;

    public String getMessage() {
        return mMessage;
    }

    @ErrorCode.Code
    public int getCode() {
        return code;
    }

    public AigError setCode(@ErrorCode.Code int errorCode) {
        this.code = errorCode;
        return this;
    }

    public AigError setMessage(String message) {
        this.mMessage = message;
        return this;
    }

}
