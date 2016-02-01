package com.mobile.newFramework.rest.errors;

import android.annotation.SuppressLint;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Error codes used in the response events.
 */
public class ErrorCode {

    public static final int NO_ERROR = 0;
    public static final int UNKNOWN_ERROR = 1;
    public static final int NO_CONNECTIVITY = 2;
    public static final int CONNECT_ERROR = 3;
    public static final int TIME_OUT = 4;
    public static final int ERROR_PARSING_SERVER_DATA = 5;
    public static final int HTTP_PROTOCOL = 6;
    public static final int IO = 7;
    public static final int SSL = 443;
    public static final int EMPTY_ENTITY = 8;
    public static final int HTTP_STATUS = 9;
    public static final int REQUEST_ERROR = 10;
    public static final int AUTO_COUNTRY_SELECTION = 11;
    public static final int INTERNAL_ERROR = 101;
    public static final int REQUIRES_USER_INTERACTION = 201;
    public static final int SERVER_IN_MAINTENANCE = 503;
    public static final int NO_COUNTRIES_CONFIGS = 12;
    public static final int NO_COUNTRY_CONFIGS_AVAILABLE = 13;
    public static final int SERVER_OVERLOAD = 429;
    public static final int CUSTOMER_NOT_LOGGED_IN = 231;

    @IntDef({
            NO_ERROR,
            UNKNOWN_ERROR,
            NO_CONNECTIVITY,
            CONNECT_ERROR,
            TIME_OUT,
            ERROR_PARSING_SERVER_DATA,
            HTTP_PROTOCOL,
            IO,
            SSL,
            EMPTY_ENTITY,
            HTTP_STATUS,
            REQUEST_ERROR,
            AUTO_COUNTRY_SELECTION,
            INTERNAL_ERROR,
            REQUIRES_USER_INTERACTION,
            SERVER_IN_MAINTENANCE,
            NO_COUNTRIES_CONFIGS,
            NO_COUNTRY_CONFIGS_AVAILABLE,
            SERVER_OVERLOAD,
            CUSTOMER_NOT_LOGGED_IN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Code {}

    @SuppressLint("SwitchIntDef")
    public static boolean isNetworkError(@Code int error) {
        switch (error) {
            case NO_CONNECTIVITY:
            case SERVER_IN_MAINTENANCE:
            case CONNECT_ERROR:
            case TIME_OUT:
            case SSL:
            case IO:
            case SERVER_OVERLOAD:
            case HTTP_STATUS:
                return true;
            default:
                return false;
        }
    }
}
