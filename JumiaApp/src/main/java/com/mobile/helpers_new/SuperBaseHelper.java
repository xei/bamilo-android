package com.mobile.helpers_new;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.requests.RequestBundle;

import java.util.HashMap;
import java.util.Map;


public abstract class SuperBaseHelper {

    private static final String TAG = SuperBaseHelper.class.getSimpleName();

    protected IResponseCallback mRequester;

    /**
     * NEW
     */

    public void sendRequest(EventType type, Map<String, String> args, IResponseCallback requester) {
        mRequester = requester;

        onPreRequest(args);

        onRequest(type, args);
    }

    public void onPreRequest(Map<String, String> args) {
        // ...
    }

    public void onRequest(EventType type, Map<String, String> data) {
        // Create request bundle
        RequestBundle requestBundle = new RequestBundle.Builder()
                .setUrl(RemoteService.completeUri(Uri.parse(type.action)).toString())
                .setCache(type.cacheTime)
                .setData(data)
                .build();
        //
        execute(requestBundle);
    }

    public void execute(RequestBundle requestBundle) {
        // ...
    }


    public static Map<String, String> convertBundleToMap(Bundle bundle) {
        Map<String, String> data = new HashMap<>();
        for (String key : bundle.keySet()) {
            data.put(key, bundle.getString(key));
        }
        return data;
    }

}
