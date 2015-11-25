package com.mobile.newFramework.rest.errors;

import retrofit.RetrofitError.Kind;

/**
 * AIG Error
 * @author pcarvalho
 */
public class AigError {

    private Kind mKind;
    private String mMessage;
    @ErrorCode.Code
    private int code;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    @ErrorCode.Code
    public int getCode() {
        return code;
    }

    public void setCode(@ErrorCode.Code int errorCode) {
        this.code = errorCode;
    }

    public Kind getKind() {
        return mKind;
    }

    public void setKind(Kind kind) {
        this.mKind = kind;
    }

}
