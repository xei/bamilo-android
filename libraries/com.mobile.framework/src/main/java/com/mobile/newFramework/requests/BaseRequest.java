package com.mobile.newFramework.requests;

import android.support.annotation.NonNull;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.errors.AigBaseException;
import com.mobile.newFramework.rest.errors.AigError;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import java.lang.reflect.InvocationTargetException;
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

    public void execute(String name){
        // Get api service via string
        Method method = AigApiInterface.Service.getMethod(name);
        // Set parameters
        List<Object> parameters = new ArrayList();
        // Add request path
        if (TextUtils.isNotEmpty(mRequestBundle.getPath())) {
            parameters.add(mRequestBundle.getPath());
        }
        // Add request data
        if(mRequestBundle.hasData()){
            parameters.add(mRequestBundle.getData());
        }
        // Add callback
        parameters.add(this);
        // Invoke api service
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        try {
            method.invoke(service, parameters.toArray());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
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
        if (mRequestBundle.isDiscardedResponse() || this.mRequester == null) {
            Print.d("REQUESTER IS NULL OR IS TO DISCARDED RESPONSE");
        }
        // Validate success response
        else if (baseResponse.hadSuccess()) {
            this.mRequester.onRequestComplete(baseResponse);
        }
        // Validate error response
        else {
            AigError aigError = new AigError();
            aigError.setCode(ErrorCode.REQUEST_ERROR);
            baseResponse.setError(aigError);
            this.mRequester.onRequestError(baseResponse);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Print.d("BASE ERROR CAUSE CODE: " + ((AigBaseException) error.getCause()).getError().getCode());
        // Validate requester and discard flag
        if (mRequestBundle.isDiscardedResponse() || this.mRequester == null) {
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
