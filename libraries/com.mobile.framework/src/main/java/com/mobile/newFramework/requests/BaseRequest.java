package com.mobile.newFramework.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobile.framework.ErrorCode;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigBaseException;
import com.mobile.newFramework.rest.JumiaError;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseRequest<T> implements Callback<BaseResponse<T>> {

    protected final Context mContext;

    protected final RequestBundle mRequestBundle;

    protected final AigResponseCallback mRequester;

    /*
     * ############## REQUEST ##############
     */

    public BaseRequest(@NonNull Context context, @NonNull RequestBundle requestBundle, AigResponseCallback requester) {
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
        System.out.println("BASE SUCCESS: " + response.getBody() + " " + baseResponse.hadSuccess());
        // Validate requester and discard flag
        if (mRequestBundle.isDiscardedResponse() || this.mRequester == null) {
            System.out.println("REQUESTER IS NULL OR IS TO DISCARDED RESPONSE");
        }
        // Validate success response
        else if (baseResponse.hadSuccess()) {
            this.mRequester.onRequestComplete(baseResponse);
        }
        // Validate error response
        else {
            JumiaError jumiaError = new JumiaError();
            jumiaError.setErrorCode(ErrorCode.REQUEST_ERROR);
            baseResponse.setError(jumiaError);
            this.mRequester.onRequestError(baseResponse);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println("BASE ERROR CAUSE CODE: " + ((AigBaseException) error.getCause()).getError().getErrorCode());
        // Validate requester and discard flag
        if (mRequestBundle.isDiscardedResponse() || this.mRequester == null) {
            System.out.println("REQUESTER IS NULL OR IS TO DISCARDED RESPONSE");
        }
        // Error response
        else {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setError(((AigBaseException) error.getCause()).getError());
            this.mRequester.onRequestError(errorResponse);
        }
    }

    /*
     * ############## UTILS TEMPORARY ##############
     */

    protected String[] getBaseAndEndPointFrom(String fullUrl) {
        String baseUrl = fullUrl;
        String endPoint = "";
        if (baseUrl.contains("/mobapi/")) {
            baseUrl = mRequestBundle.getUrl().substring(0, baseUrl.indexOf("/mobapi/") + "/mobapi/".length());
            endPoint = mRequestBundle.getUrl().substring(baseUrl.indexOf("/mobapi/") + "/mobapi/".length());
        }
        return new String[]{baseUrl, endPoint};
    }
}
