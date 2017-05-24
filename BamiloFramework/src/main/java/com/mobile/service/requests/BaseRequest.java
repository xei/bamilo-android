package com.mobile.service.requests;

import android.support.annotation.NonNull;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.AigRestAdapter;
import com.mobile.service.rest.errors.AigBaseException;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.rest.interfaces.AigResponseCallback;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public void execute(String name) {
        // Get api service via string
        Method method = AigApiInterface.Service.getMethod(name);
        // Set parameters
        List<Object> parameters = new ArrayList<>();
        // Add request path
        if (TextUtils.isNotEmpty(mRequestBundle.getPath())) parameters.add(mRequestBundle.getPath());
        // Add request data
        if(CollectionUtils.isNotEmpty(mRequestBundle.getData())) parameters.add(mRequestBundle.getData());
        // Add request array
        if(CollectionUtils.isNotEmpty(mRequestBundle.getArray())) parameters.add(mRequestBundle.getArray());
        // Add callback
        parameters.add(this);
        // Invoke api service
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle).create(AigApiInterface.class);
        try {
            method.invoke(service, parameters.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * ############## RESPONSE ##############
     */

    @Override
    public void success(BaseResponse baseResponse, Response response) {
        Print.d("BASE SUCCESS: " + response.getBody() + " " + baseResponse.hadSuccess());
        // Validate requester and discard flag
        if (mRequestBundle.discardResponse() || this.mRequester == null) {
            Print.d("REQUESTER IS NULL OR IS TO DISCARDED RESPONSE");
        }
        // Validate success response
        else if (baseResponse.hadSuccess()) {
            this.mRequester.onRequestComplete(baseResponse);
        }
        // Validate error response
        else {
            this.mRequester.onRequestError(baseResponse);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Print.d("BASE ERROR CAUSE CODE: " + ((AigBaseException) error.getCause()).getError().getCode());
        // Validate requester and discard flag
        if (mRequestBundle.discardResponse() || this.mRequester == null) {
            Print.d("REQUESTER IS NULL OR IS TO DISCARDED RESPONSE");
        }
        // Error response
        else {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setError(((AigBaseException) error.getCause()).getError());
            this.mRequester.onRequestError(errorResponse);
        }
    }

}