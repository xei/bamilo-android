package com.mobile.interfaces;

import com.mobile.newFramework.pojo.BaseResponse;

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
    void onRequestComplete(BaseResponse baseResponse);

    /**
     * Handles the error request
     */
    void onRequestError(BaseResponse baseResponse);
}