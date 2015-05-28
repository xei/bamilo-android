package com.mobile.helpers;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.RequestBundle;

import java.util.HashMap;
import java.util.Map;


public abstract class SuperBaseHelper implements AigResponseCallback {

    protected IResponseCallback mRequester;

    protected EventType mEventType;


    public SuperBaseHelper(){
        mEventType = getEventType();
    }

    public void sendRequest(Bundle args, IResponseCallback requester) {
        mRequester = requester;
        RequestBundle requestBundle = createRequest(args);
        onRequest(requestBundle);
    }

    protected RequestBundle createRequest(Bundle args) {
        // Create builder
        RequestBundle.Builder requestBundleBuilder = new RequestBundle.Builder()
                .setUrl(getRequestUrl(args))
                .setCache(mEventType.cacheTime);
        // Validate data
        if (args != null) {
            requestBundleBuilder.setData(getRequestData(args));
        }
        // Validate priority
        if(!isPrioritary()){
            requestBundleBuilder.discardResponse();
        }
        //
        return requestBundleBuilder.build();
    }

    protected String getRequestUrl(Bundle args) {
        return RemoteService.completeUri(Uri.parse(mEventType.action)).toString();
    }

    protected Map<String, String> getRequestData(Bundle args) {
        return convertBundleToMap(args);
    }

    public boolean isPrioritary(){
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




}
