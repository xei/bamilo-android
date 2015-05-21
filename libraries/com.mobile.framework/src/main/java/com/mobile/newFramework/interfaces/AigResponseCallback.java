package com.mobile.newFramework.interfaces;

import com.mobile.newFramework.pojo.BaseResponse;

public interface AigResponseCallback {

    void onRequestComplete(BaseResponse baseResponse);

    void onRequestError(BaseResponse baseResponse);
}