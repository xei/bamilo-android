package com.mobile.pojo;

import android.support.annotation.NonNull;

/**
 * Interface used to support custom views for dynamic form items.
 *
 * @author spereira
 */
public interface ICustomFormField {

    /**
     * Add a custom view to represent the form field.
     */
    void addCustomView(@NonNull ICustomFormFieldView custom);

}
