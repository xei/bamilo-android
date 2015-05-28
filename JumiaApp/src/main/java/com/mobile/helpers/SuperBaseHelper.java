package com.mobile.helpers;

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

    protected EventType eventType;

    public SuperBaseHelper(){
        eventType = getEventType();
    }

    public void sendRequest(Bundle args, IResponseCallback requester) {
        mRequester = requester;

        RequestBundle requestBundle = createRequest(args);

        onPreRequest(args);

        onRequest(requestBundle);
    }

    protected void onPreRequest(Bundle args) {
    }

    protected RequestBundle createRequest(Bundle args) {
        RequestBundle.Builder requestBundleBuilder = new RequestBundle.Builder()
                .setUrl(RemoteService.completeUri(Uri.parse(eventType.action)).toString())
                .setCache(eventType.cacheTime);

        if(args != null) {
            requestBundleBuilder.setData(convertBundleToMap(args));
        }

        if(!isPrioritary()){
            requestBundleBuilder.discardResponse();
        }

        return requestBundleBuilder.build();
    }

    protected abstract void onRequest(RequestBundle requestBundle);

    public static Map<String, String> convertBundleToMap(Bundle bundle) {
        Map<String, String> data = new HashMap<>();
        for (String key : bundle.keySet()) {
            data.put(key, bundle.getString(key));
        }
        return data;
    }

    public abstract EventType getEventType();

    public boolean isPrioritary(){
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }


}
