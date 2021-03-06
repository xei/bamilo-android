package com.bamilo.android.appmodule.bamiloapp.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.bamilo.android.framework.components.customfontviews.CheckBox;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.framework.service.forms.IFormField;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.appmodule.bamiloapp.pojo.IDynamicFormItemField;
import com.bamilo.android.R;

/**
 * Class used to represent check box form field.
 * @author spereira
 *
 * TODO: Improve this component, NAFAMZ-16058
 *
 */
public class CheckBoxField extends DynamicFormItem implements IDynamicFormItemField {

    public CheckBoxField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // Case form NEWSLETTER
        @LayoutRes int layout;
        if (parent.getForm().getType() == FormConstants.NEWSLETTER_PREFERENCES_FORM) {
            layout = R.layout._def_form_checkbox_right;
        } else {
            layout = R.layout.gen_form_check_box;
        }
        buildCheckBox(params, layout);
    }

    @Override
    public boolean validate(boolean fallback) {
        if (this.entry.getValidation().isRequired()) {
            fallback = ((CheckBox) this.dataControl).isChecked();
        }
        return fallback;
    }

    @Override
    public void save(@NonNull ContentValues values) {
        // Case contains a non empty value for checked state.
        if (!TextUtils.isEmpty(this.entry.getValue()) && parent.getForm().getType() == FormConstants.NEWSLETTER_PREFERENCES_FORM) {
            if (((CheckBox) this.dataControl).isChecked()) {
                values.put(getName(), this.entry.getValue());
                return;
            }
        }
        // Otherwise get the default behavior.
        this.getDefaultValue(values);
    }

    @Override
    public void loadState(@NonNull Bundle inStat) {
        ((CheckBox) this.dataControl).setChecked(inStat.getBoolean(getKey()));
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putBoolean(getKey(), ((CheckBox) this.dataControl).isChecked());
    }

    @Override
    public void select() {
        this.dataControl.performClick();
    }

    /**
     * Build check box
     */
    private void buildCheckBox(RelativeLayout.LayoutParams params, @LayoutRes int layout) {
        this.control.setLayoutParams(params);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        // Default or right
        if(layout == R.layout.gen_form_check_box) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        } else {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        //#RTL
        if (ShopSelector.isRtl()) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        //Mandatory control
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setId(parent.getNextId());
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(ContextCompat.getColor(context, R.color.orange_lighter));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);



        // Default or right
        if(layout == R.layout.gen_form_check_box) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        //Data control
        int formPadding = context.getResources().getDimensionPixelOffset(R.dimen.form_check_padding);
        params.leftMargin = formPadding;
        params.rightMargin = formPadding;

        // Default or right
        final CheckBox checkBox;
        if(layout == R.layout.gen_form_check_box) {
            checkBox = (CheckBox) View.inflate(this.context, layout, null);
        } else {
            checkBox = (CheckBox) LayoutInflater.from(this.context).inflate(layout, dataContainer, false);
        }

        // Get check box
        this.dataControl = checkBox;
        this.dataControl.setId(parent.getNextId());

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        //#RTL
        if(ShopSelector.isRtl()){
            params.addRule(RelativeLayout.RIGHT_OF,this.mandatoryControl.getId());
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }else{
            params.addRule(RelativeLayout.LEFT_OF,this.mandatoryControl.getId());
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }

        // Default or right
        if(layout == R.layout.gen_form_check_box) {
            this.dataControl.setLayoutParams(params);
        }

        this.dataControl.setContentDescription(this.entry.getKey());
        this.dataControl.setFocusable(false);
        this.dataControl.setFocusableInTouchMode(false);
        checkBox.setText(this.entry.getLabel().length() > 0 ? this.entry.getLabel() : this.context.getString(R.string.register_text_terms_a) + " " + this.context.getString(R.string.register_text_terms_b));
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.context.getResources().getDimension(R.dimen.form_checkbox_text_size));
        // Set default value
        if (Boolean.parseBoolean(this.entry.getValue()) || entry.isChecked()) {
            checkBox.setChecked(true);
        }

        // Validate disabled flag
        if (this.entry.isDisabledField()) {
            checkBox.setEnabled(false);
            disableView(checkBox);
            // Fix issue with Marshmallow to select correct drawable
            checkBox.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false);
                    checkBox.refreshDrawableState();
                }
            },250);

        }

        // Add
        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);
        this.control.addView(dataContainer);

    }


}
