package com.mobile.pojo;

import android.support.annotation.NonNull;

/**
 * Interface used support custom views for dynamic form items.
 *
 * @author spereira
 */
public interface ICustomFormField {

    /**
     * Add a custom view to represent the form field.
     */
    void addCustomView(@NonNull ICustomFormFieldView custom);

}
