package com.mobile.classes.models;

/**
 * Created by narbeh on 12/5/17.
 */

public final class LoginEventModel extends BaseEventModel {
    public boolean isSuccess;
    public String method;
    public String emailDomain;
    public int customerId;

    public LoginEventModel(boolean isSuccess, String method, String emailDomain, int customerId) {
        this.isSuccess = isSuccess;
        this.method = method;
        this.emailDomain = emailDomain;
        this.customerId = customerId;
    }
}
