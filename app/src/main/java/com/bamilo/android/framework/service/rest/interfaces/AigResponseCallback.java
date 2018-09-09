package com.bamilo.android.framework.service.rest.interfaces;

import com.bamilo.android.framework.service.pojo.BaseResponse;

public interface AigResponseCallback {

    void onRequestComplete(BaseResponse baseResponse);

    void onRequestError(BaseResponse baseResponse);
}