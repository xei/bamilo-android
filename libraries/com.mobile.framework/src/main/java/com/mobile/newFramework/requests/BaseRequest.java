package com.mobile.newFramework.requests;

import android.support.annotation.NonNull;

import com.mobile.framework.ErrorCode;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.errors.AigBaseException;
import com.mobile.newFramework.rest.errors.JumiaError;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseRequest<T> implements Callback<BaseResponse<T>> {

    protected final RequestBundle mRequestBundle;

    protected final AigResponseCallback mRequester;

    /*
     * ############## REQUEST ##############
     */

    public BaseRequest(@NonNull RequestBundle requestBundle, AigResponseCallback requester) {
        this.mRequestBundle = requestBundle;
        this.mRequester = requester;
        AigApiInterface.Service.init();
    }

    public void execute(){}

    public void execute(String name){
        long tStart = System.currentTimeMillis();
        Method method = AigApiInterface.Service.getMethod(name);
        List parameters = new LinkedList();
        if(mRequestBundle.getData() != null){
            parameters.add(mRequestBundle.getData());
        }
        parameters.add(this);
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        try {
            method.invoke(service, parameters.toArray());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        System.out.println("elapsed time: " + tDelta);
    }

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
