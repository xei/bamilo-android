package com.mobile.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.pojo.IDynamicFormItemField;
import com.mobile.utils.RadioGroupLayoutVertical;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.Map;

/**
 * Class used to create a form field represented by a switch with radio group.
 * @author spereira
 */
public class SwitchRadioField extends DynamicFormItem implements IDynamicFormItemField, CompoundButton.OnCheckedChangeListener {

    private RadioGroupLayoutVertical mRelatedRadioGroup;
    private SwitchCompat mSwitchButton;

    /**
     * The constructor for the DynamicFormItem
     */
    public SwitchRadioField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    /**
     * Build the field view
     */
    @Override
    public  void build(@NonNull RelativeLayout.LayoutParams params) {
        // Get field container
        ViewGroup container = (ViewGroup) View.inflate(context, R.layout.gen_form_switch_radio, null);
        // Build related field
        mRelatedRadioGroup = buildVerticalRadioGroup(container, entry.getRelatedField(), params);
        // Get switch button
        mSwitchButton = (SwitchCompat) container.findViewById(R.id.switch_field);
        // Set text
        TextView v8SwitchTextView = (TextView) container.findViewById(R.id.switch_text);
        if(v8SwitchTextView != null) v8SwitchTextView.setText(entry.getLabel());
        else mSwitchButton.setText(entry.getLabel());
        // Set listener
        mSwitchButton.setOnCheckedChangeListener(this);
        // Set default value using performing click event
        mSwitchButton.setChecked(!entry.isChecked());
        mSwitchButton.performClick();
        // Add view control
        dataControl = mSwitchButton;
        // Add view
        control.addView(container, params);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Set related state
        UIUtils.setVisibility(mRelatedRadioGroup, isChecked);
    }

    /**
     * Generates a Vertical RadioGroup
     */
    private RadioGroupLayoutVertical buildVerticalRadioGroup(@NonNull ViewGroup container, @NonNull IFormField entry, RelativeLayout.LayoutParams params) {
        // Get group
        RadioGroupLayoutVertical radioGroup = (RadioGroupLayoutVertical) View.inflate(container.getContext(), R.layout.form_radiolistlayout, null);
        radioGroup.setLayoutParams(params);
        // Set divider
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable._gen_divider_horizontal_black_400));
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        // Right position style
        radioGroup.enableRightStyle();
        // Set options
        radioGroup.setItems(entry.getDataSet(), entry.getValue());
        // Add view
        container.addView(radioGroup, params);
        // Return
        return radioGroup;
    }

    /**
     * Validate field
     */
    @Override
    public boolean validate(boolean fallback) {
        return fallback;
    }

    /**
     * Save field value
     */
    @Override
    public void save(@NonNull ContentValues values) {
        // Case checked then get radio button option
        if (mSwitchButton.isChecked()) {
            String value = mRelatedRadioGroup.getSelectedFieldValue();
            for (Map.Entry<String, String> entryValue : entry.getRelatedField().getDataSet().entrySet()) {
                if (entryValue.getValue().equals(value)) {
                    values.put(getName(), entryValue.getKey());
                    break;
                }
            }
        }
        // Case unchecked then put invalid value
        else {
            values.put(getName(), IntConstants.DEFAULT_POSITION);
        }
    }

    @Override
    public void select() {
        mSwitchButton.performClick();
    }

    @Override
    public void loadState(@NonNull Bundle inStat) {
        mSwitchButton.setChecked(inStat.getBoolean(getKey()));
        mRelatedRadioGroup.setSelection(inStat.getInt(getKey() + "_" + entry.getRelatedField().getKey()));
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putBoolean(getKey(), mSwitchButton.isChecked());
        outState.putInt(getKey() + "_" + entry.getRelatedField().getKey(), mRelatedRadioGroup.getSelectedIndex());
    }

}
