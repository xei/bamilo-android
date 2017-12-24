package com.mobile.view.components;

import android.content.Context;
import android.view.View;

public abstract class BaseViewComponent<T> {

    String mPage;
    int mInstanceIndex;

    public abstract View getView(Context context);

    public abstract void setContent(T content);

    public void enableTracking(String page, int instanceIndex) {
        this.mPage = page;
        this.mInstanceIndex = instanceIndex;
    }
}
