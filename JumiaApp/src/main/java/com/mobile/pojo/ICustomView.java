package com.mobile.pojo;

import android.view.View;

/**
 * Interface used support custom views for dynamic form items.
 * @author spereira
 */
public interface ICustomView {

    /**
     * Add a custom view to represent the form field.
     */
    void addCustomView(View custom);

}
