package com.mobile.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mobile.service.forms.FormField;
import com.mobile.service.forms.IFormField;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.IDynamicFormItemField;
import com.mobile.utils.RadioGroupExpandable;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.view.R;

/**
 * Class used to represent Radio Expandable field
 *
 */
public class RadioExpandableField extends DynamicFormItem implements IDynamicFormItemField, View.OnClickListener {

    public RadioExpandableField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // Get field container
        for (FormField field  : parent.getForm().getFields()) {
            // Build related field
            buildVerticalRadioGroup(this.control, field, params);
        }
    }

    @Override
    public boolean validate(boolean fallback) {
        if (this.entry.getValidation().isRequired()) {
            fallback = this.dataControl.isSelected();
        }
        fallback &= ((RadioGroupExpandable) this.dataControl).validate();
        return fallback;
    }


    @Override
    public void loadState(@NonNull Bundle inStat) {
        ((RadioGroupExpandable) this.dataControl).loadState(getEntry().getKey(),inStat);
    }

    @Override
    public void saveState(@NonNull Bundle outState) {

        mPreSelectedPosition = ((RadioGroupExpandable) this.dataControl).getSelectedIndex();
        if(mPreSelectedPosition > RadioGroupLayout.NO_DEFAULT_SELECTION){
            ((RadioGroupExpandable) this.dataControl).saveState(getEntry().getKey(), outState);
        }
    }

    @Override
    public void save(@NonNull ContentValues values) {

        mPreSelectedPosition = ((RadioGroupExpandable) this.dataControl).getSelectedIndex();
        if(mPreSelectedPosition > RadioGroupLayout.NO_DEFAULT_SELECTION){
            ((RadioGroupExpandable) this.dataControl).save(getEntry().getName(), values);
        }
    }

    @Override
    public boolean validate() {
        return ((RadioGroupExpandable) this.dataControl).validate();
    }

    @Override
    public void select() {
        this.dataControl.performClick();
    }


    /**
     * Generates a Vertical RadioGroup
     */
    private RadioGroupExpandable buildVerticalRadioGroup(@NonNull ViewGroup container, @NonNull IFormField entry, RelativeLayout.LayoutParams params) {
        // Get group
        RadioGroupExpandable radioGroup = (RadioGroupExpandable) View.inflate(container.getContext(), R.layout.form_radio_expandable_layout, null);
        // Add parent click listener
        radioGroup.addClickListener(this);
        radioGroup.setLayoutParams(params);
        // Set options
        radioGroup.setItems(entry.getOptions(), entry.getValue());
        this.dataControl = radioGroup;
        // Add view
        container.addView(radioGroup, params);

        // Return
        return radioGroup;
    }

    @Override
    public void onClick(View v) {
        parent.getClickListener().get().onClick(v);
    }
}
