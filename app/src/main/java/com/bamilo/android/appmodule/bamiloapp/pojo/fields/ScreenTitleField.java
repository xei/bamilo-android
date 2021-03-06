package com.bamilo.android.appmodule.bamiloapp.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import android.widget.TextView;
import com.bamilo.android.framework.service.forms.IFormField;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.appmodule.bamiloapp.pojo.IDynamicFormItemField;
import com.bamilo.android.R;

/**
 * Class used to represent a screen title field.
 * @author spereira
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
