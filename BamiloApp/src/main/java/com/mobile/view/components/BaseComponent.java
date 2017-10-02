package com.mobile.view.components;

import android.content.Context;
import android.view.View;

public interface BaseComponent <T> {
    View getView(Context context);

    void setContent(T content);
}
