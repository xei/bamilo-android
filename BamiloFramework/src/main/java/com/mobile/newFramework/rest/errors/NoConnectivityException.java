package com.mobile.newFramework.rest.errors;

import java.io.IOException;

/**
 * No Network Exception
 */
public class NoConnectivityException extends IOException {
    public NoConnectivityException() {
        super("No network connectivity!");
    }
}