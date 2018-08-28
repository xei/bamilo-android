package com.bamilo.android.appmodule.bamiloapp.interfaces;

import com.bamilo.android.framework.service.pojo.BaseResponse;

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