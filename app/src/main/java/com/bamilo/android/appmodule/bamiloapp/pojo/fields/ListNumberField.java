package com.bamilo.android.appmodule.bamiloapp.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.bamilo.android.framework.service.forms.IFormField;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.appmodule.bamiloapp.pojo.ICustomFormField;
import com.bamilo.android.appmodule.bamiloapp.pojo.ICustomFormFieldView;
import com.bamilo.android.appmodule.bamiloapp.pojo.IDynamicFormItemField;

/**
 * Class used to represent a list number field.
 * @author spereira
 */
public class ListNumberField extends DynamicFormItem implements IDynamicFormItemField, ICustomFormField {

    private ICustomFormFieldView mCustomView;

    /**
     * The constructor
     */
    public ListNumberField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // ...
    }

    @Override
    public void select() {
        // ...
    }

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

    @Nullable
    public String getValueFromCustomView() {
        return mCustomView != null ? mCustomView.save() : null;
    }
}
