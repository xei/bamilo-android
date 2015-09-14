package com.mobile.interfaces;

import android.os.Bundle;

/**
 * Callback for the response
 *
 * @author Guilherme Silva
 *
 */
public interface IResponseCallback {
    /**
     * Handles the success request
     */
    void onRequestComplete(Bundle bundle);

    /**
     * Handles the error request
     */
    void onRequestError(Bundle bundle);
}