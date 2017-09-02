package com.mobile.pojo;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

/**
 * Temporary interface until DynamicFormItem with new approach.
 * @author spereira
 */
public interface IDynamicFormItemField {

    /**
     * Build the a view for this form field.
     */
    void build(@NonNull RelativeLayout.LayoutParams params);

    /**
     * Validate the form field.
     */
    boolean validate(boolean fallback);

    /**
     * Save the form field into values.
     */
    void save(@NonNull ContentValues values);

    /**
     * Perform the select action.
     */
    void select();

    /**
     * Save the form state into bundle.
     */
    void saveState(@NonNull Bundle outState);

    /**
     * Load the saved state from bundle.
     */
    void loadState(@NonNull Bundle inStat);

}
