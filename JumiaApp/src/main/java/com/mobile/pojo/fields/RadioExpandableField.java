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
        this.errorControl = createErrorControl(container.getId(), RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Add view
        container.addView(radioGroup, params);

        // Return
        return radioGroup;
    }

    private View createErrorControl(int dataControlId, int controlWidth) {
        ViewGroup errorControl;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, dataControlId);

        errorControl = new RelativeLayout(this.context);
        errorControl.setId(parent.getNextId());
        errorControl.setLayoutParams(params);
        errorControl.setVisibility(View.GONE);


        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        //#RTL
        if (ShopSelector.isRtl()) {
            params.addRule(RelativeLayout.RIGHT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, (int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0);
        } else {
            params.addRule(RelativeLayout.LEFT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins((int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0, 0, 0);
        }

        ImageView errImage = new ImageView(this.context);
        errImage.setId(parent.getNextId());
        errImage.setLayoutParams(params);
        errImage.setImageResource(R.drawable.indicator_input_error);

        //ErrorText params
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        //#RTL
        if (ShopSelector.isRtl()) {
            params.addRule(RelativeLayout.LEFT_OF, errImage.getId());
            params.setMargins(0, 0, 5, 0);
        } else {
            params.addRule(RelativeLayout.RIGHT_OF, errImage.getId());
            params.setMargins(5, 0, 0, 0);
        }


        this.errorTextControl = new TextView(this.context);
        this.errorTextControl.setId(parent.getNextId());
        this.errorTextControl.setText(this.errorText);
        this.errorTextControl.setLayoutParams(params);
        this.errorTextControl.setTextColor(errorColor);
        this.errorTextControl.setTextSize(ERRORTEXTSIZE);

        //#RTL
        if (ShopSelector.isRtl()) {
            this.errorTextControl.setSingleLine(true);
            this.errorTextControl.setEllipsize(TextUtils.TruncateAt.END);
        }

        errorControl.addView(this.errorTextControl);
        errorControl.addView(errImage);

        return errorControl;

    }

    @Override
    public void onClick(View v) {
        parent.getClickListener().get().onClick(v);
    }
}
