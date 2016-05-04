package com.mobile.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.IDynamicFormItemField;
import com.mobile.utils.RadioGroupExpandable;
import com.mobile.utils.RadioGroupLayoutVertical;
import com.mobile.view.R;

import java.lang.ref.WeakReference;

/**
 * Class used to represent Radio Expandable field
 *
 */
public class RadioExpandableField extends DynamicFormItem implements IDynamicFormItemField, View.OnClickListener {

    private static final java.lang.String TAG = RadioExpandableField.class.getName();

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

        Print.i("code1subform : validate final result "+fallback);

        return fallback;
    }


    @Override
    public void loadState(@NonNull Bundle inStat) {
        ((RadioGroupExpandable) this.dataControl).setSelection(inStat.getInt(getKey()));
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putInt(getKey(), ((RadioGroupExpandable) this.dataControl).getSelectedIndex());
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
