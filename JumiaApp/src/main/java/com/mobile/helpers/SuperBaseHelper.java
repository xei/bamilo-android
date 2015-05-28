package com.mobile.helpers;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public abstract class SuperBaseHelper implements AigResponseCallback {

    protected IResponseCallback mRequester;

    protected EventType mEventType;

    private EventTask mEventTask;

    public SuperBaseHelper(){
        mEventType = getEventType();
    }

    public final void sendRequest(Bundle args, IResponseCallback requester) {
        mRequester = requester;

        Serializable evenTask = args.getSerializable(Constants.BUNDLE_EVENT_TASK);
        mEventTask = evenTask instanceof EventTask ? (EventTask)evenTask : setEventTask();

        RequestBundle requestBundle = createRequest(args);
        onRequest(requestBundle);
    }

    protected RequestBundle createRequest(Bundle args) {
        // Create builder
        RequestBundle.Builder requestBundleBuilder = new RequestBundle.Builder()
                .setUrl(getRequestUrl())
                .setCache(mEventType.cacheTime);
        // Validate data
        if (args != null) {
            requestBundleBuilder.setData(getRequestData(args));
        }
        // Validate priority
        if(!hasPriority()){
            requestBundleBuilder.discardResponse();
        }
        //
        return requestBundleBuilder.build();
    }

    protected String getRequestUrl() {
        return RemoteService.completeUri(Uri.parse(mEventType.action)).toString();
    }

    protected Map<String, String> getRequestData(Bundle args) {
        return convertBundleToMap(args);
    }

    public boolean hasPriority(){
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }

    protected abstract void onRequest(RequestBundle requestBundle);

    public abstract EventType getEventType();

    /*
     * #### TODO: FIX THIS APPROACH -> USE ONLY ONE STRUCTURE
     */

    public static Map<String, String> convertBundleToMap(Bundle bundle) {
        Map<String, String> data = new HashMap<>();
        for (String key : bundle.keySet()) {
            data.put(key, bundle.getString(key));
        }
        return data;
    }

    public static Map<String, String> convertContentValuesToMap(ContentValues bundle) {
        Map<String, String> data = new HashMap<>();
        for (String key : bundle.keySet()) {
            data.put(key, bundle.getAsString(key));
        }
        return data;
    }

    public EventTask getEventTask(){
        if(mEventTask == null){
            mEventTask = setEventTask();
        }
        return mEventTask;
    }

    protected abstract EventTask setEventTask();


    public Bundle generateSuccessBundle(BaseResponse baseResponse){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, hasPriority());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, getEventTask());
        return bundle;
    }

    public Bundle generateErrorBundle(BaseResponse baseResponse){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.error.getErrorCode());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}
