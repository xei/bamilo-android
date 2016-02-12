package com.mobile.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.IDynamicFormItemField;
import com.mobile.view.R;

/**
 * Class used to represent a screen title field.
 */
public class ScreenTitleField extends DynamicFormItem implements IDynamicFormItemField {

    /**
     * The constructor for the DynamicFormItem
     */
    public ScreenTitleField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    /**
     * Build the field view
     */
    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // Get field container
        View view = View.inflate(context, R.layout._def_gen_form_screen_title, null);
        // Get title
        ((TextView) view.findViewById(R.id.title)).setText(entry.getLabel());
        // Get sub title
        ((TextView) view.findViewById(R.id.sub_title)).setText(entry.getSubLabel());
        // Add view
        this.getControl().addView(view, params);
    }

    /**
     * Validate field
     */
    @Override
    public boolean validate(boolean fallback) {
        return fallback;
    }

    @Override
    public void save(@NonNull ContentValues values) {
        // ...
    }

    @Override
    public void select() {
        // ...
    }

    @Override
    public void loadState(@NonNull Bundle inStat) {
        // ...
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        // ...
    }

}
