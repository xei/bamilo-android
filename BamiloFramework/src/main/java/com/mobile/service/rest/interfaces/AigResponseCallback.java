package com.mobile.service.rest.interfaces;

import com.mobile.service.pojo.BaseResponse;

public interface AigResponseCallback {

    void onRequestComplete(BaseResponse baseResponse);

    void onRequestError(BaseResponse baseResponse);
}