package com.mobile.helpers_new;

import android.os.Bundle;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.interfaces.AigResponseCallback;


public abstract class BaseHelper implements AigResponseCallback {

    public void onPreRequest(Bundle args) {
        // ...
    }

    public abstract void onRequest(Bundle args, IResponseCallback callback);

    public void sendRequest(Bundle args, IResponseCallback callback) {
        onPreRequest(args);
        onRequest(args, callback);
    }

}
