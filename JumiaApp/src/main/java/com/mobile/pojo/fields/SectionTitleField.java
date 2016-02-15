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
 * Class used to represent a section title.
 * @author spereira
 */
public class SectionTitleField extends DynamicFormItem implements IDynamicFormItemField {

    /**
     * The constructor for the DynamicFormItem
     *
     * @param parent  The parent of this control ( an instance of a DynamicForm )
     * @param context The context where this control is to be inserted. If the FormItem type date should
     *                be used, an activity needs to be given, as the date type wants to open a dialog.
     * @param entry   The entry that corresponds to this control on the form return by the framework
     */
    public SectionTitleField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    /**
     * Build the field view
     */
    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // Get field container
        TextView title = (TextView) View.inflate(context, R.layout._def_gen_form_section_title, null);
        // Set field
        title.setText(entry.getLabel());
        // Add view
        this.getControl().addView(title, params);
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
