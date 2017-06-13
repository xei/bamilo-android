package com.mobile.extlibraries.emarsys;

/**
 * Created by Arash Hassanpour on 4/5/2017.
 */

public class EmarsysError {
    public String errorWithDomain;
    public String code;
    public String userInfo;


    public EmarsysError(String errorWithDomain, String code, String userInfo) {
        this.errorWithDomain = errorWithDomain;
        this.code = code;
        this.userInfo = userInfo;
    }
}
