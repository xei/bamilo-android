package com.mobile.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.mobile.newFramework.forms.IFormField;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.ICustomFormField;
import com.mobile.pojo.ICustomFormFieldView;
import com.mobile.pojo.IDynamicFormItemField;

/**
 * Class used to represent a screen title field.
 * @author spereira
 */
public class ListNumberField extends DynamicFormItem implements IDynamicFormItemField, ICustomFormField {

    private ICustomFormFieldView mCustomView;

    /**
     * The constructor for the DynamicFormItem
     */
    public ListNumberField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    /**
     * Build the field view
     */
    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // ...
    }

    @Override
    public void select() {
        // ...
    }

    /**
     * Validate field
     */
    @Override
    public boolean validate(boolean fallback) {
        return mCustomView == null || mCustomView.validate();
    }

    @Override
    public void save(@NonNull ContentValues values) {
        if (mCustomView != null) {
            values.put(getName(), mCustomView.save());
        }
    }

    @Override
    public void loadState(@NonNull Bundle inStat) {
        if (mCustomView != null) {
            mCustomView.load(inStat.getString(getName()));
        }
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        if (mCustomView != null) {
            outState.putString(getName(), mCustomView.save());
        }
    }

    @Override
    public void addCustomView(@NonNull ICustomFormFieldView custom) {
        // Save custom view
        this.mCustomView = custom;
        // Add view
        this.getControl().addView(custom.getView());
    }
}
