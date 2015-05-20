package com.mobile.newFramework.requests;

import android.content.Context;

import com.mobile.newFramework.pojo.BaseResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by spereira on 5/19/15.
 */
public abstract class BaseRequest implements Callback<BaseResponse> {

    private Callback<BaseResponse> automatedTestCallback;

    protected Context context;

    public BaseRequest(Context context) {
        this.context = context;
    }

    // TODO: Remove this
    public void setCallBack(Callback<BaseResponse> otherCallback) {
        this.automatedTestCallback = otherCallback;
    }
    
    @Override
    public void success(BaseResponse baseResponse, Response response) {
        System.out.println("BASE SUCCESS: " + response.getBody() + " " + baseResponse.success);
        if(automatedTestCallback != null) this.automatedTestCallback.success(baseResponse, response);
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println("BASE ERROR: " + error.getBody());
        if(automatedTestCallback != null) this.automatedTestCallback.failure(error);
    }
}
