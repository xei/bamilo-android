package com.mobile.newFramework.rest.errors;

import com.mobile.newFramework.ErrorCode;

import retrofit.RetrofitError.Kind;

/**
 * Created by pcarvalho on 5/22/15.
 */
public class JumiaError {

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
