package com.mobile.newFramework.requests;

import android.content.Context;

import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseRequest implements Callback<BaseResponse> {

    protected final Context mContext;

    protected final BaseRequestBundle mRequestBundle;

    protected final AigResponseCallback mRequester;

    /*
     * ############## REQUEST ##############
     */

    public BaseRequest(Context context, BaseRequestBundle requestBundle, AigResponseCallback requester) {
        this.mContext = context;
        this.mRequestBundle = requestBundle;
        this.mRequester = requester;
    }

    public abstract void execute();

    /*
     * ############## RESPONSE ##############
     */

    @Override
    public void success(BaseResponse baseResponse, Response response) {
        System.out.println("BASE SUCCESS: " + response.getBody() + " " + baseResponse.success);
        //
        if(mRequester != null) this.mRequester.onRequestComplete(baseResponse);
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println("BASE ERROR: " + error.getBody());

        if(mRequester != null) this.mRequester.onRequestError(new BaseResponse());
    }
}
