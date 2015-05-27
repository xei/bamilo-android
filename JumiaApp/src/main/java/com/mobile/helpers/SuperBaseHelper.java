package com.mobile.helpers;

import android.os.Bundle;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.interfaces.AigResponseCallback;

import java.util.HashMap;
import java.util.Map;


public abstract class SuperBaseHelper implements AigResponseCallback {

    protected IResponseCallback mRequester;

    public void sendRequest(Bundle args, IResponseCallback requester) {
        mRequester = requester;

        onPreRequest(args);

        onRequest(args);
    }

    protected void onPreRequest(Bundle args) {
        // ...
    }

    protected abstract void onRequest(Bundle args);


    public static Map<String, String> convertBundleToMap(Bundle bundle) {
        Map<String, String> data = new HashMap<>();
        for (String key : bundle.keySet()) {
            data.put(key, bundle.getString(key));
        }
        return data;
    }

}
