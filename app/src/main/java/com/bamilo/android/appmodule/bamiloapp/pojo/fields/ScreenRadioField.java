package com.bamilo.android.appmodule.bamiloapp.pojo.fields;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.bamilo.android.framework.components.customfontviews.RadioButton;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.helpers.SubmitFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.GetNewsletterUnSubscribeFormHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.IFormField;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.appmodule.bamiloapp.pojo.IDynamicFormItemField;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogFormFragment;
import com.bamilo.android.R;

/**
 * Class used to represent the screen radio field.
 * @author spereira
 */
public class ScreenRadioField extends DynamicFormItem implements IDynamicFormItemField, View.OnClickListener, IResponseCallback, DialogFormFragment.IDialogForm {

    private boolean isChecked;

    /**
     * The constructor for the DynamicFormItem
     */
    public ScreenRadioField(DynamicForm parent, Context context, IFormField entry) {
        super(parent, context, entry);
    }

    /**
     * Build the field view
     */
    @Override
    public void build(@NonNull RelativeLayout.LayoutParams params) {
        // Get field container
        View container = View.inflate(context, R.layout.gen_form_screen_radio, null);
        // Set title
        ((TextView) container.findViewById(R.id.title)).setText(entry.getLabel());
        // Set sub title
        ((TextView) container.findViewById(R.id.sub_title)).setText(entry.getSubLabel());
        // Set button
        TextView button = (TextView) container.findViewById(R.id.button);
        // Set button state
        setButtonState(button, entry.isChecked());
        // Set click behavior
        button.setOnClickListener(this);
        // Set data control view
        dataControl = button;
        // Add view
        control.addView(container);
    }

    /**
     * Set the button and the other fields.
     */
    private void setButtonState(TextView button, boolean checked) {
        isChecked = checked;
        if(isChecked) {
            // Set inactive text
            button.setText(context.getString(R.string.on_label));
            // Show other fields
            showOtherFields();
        } else {
            // Set inactive text
            button.setText(context.getString(R.string.off_label));
            // Hide other fields
            hideOtherFields();
        }
        // Notify parent
        parent.onCheckedChangeListener(new RadioButton(context), isChecked);
    }


    /**
     * Show all other fields.<br>
     * Case default value is un subscribe then is subscribed all fields performing automatically the click event.
     */
    private void showOtherFields() {
        // Subscribe all performing automatically the click event.
        boolean selectAll = !entry.isChecked();
        // Hide other fields
        parent.showAll(selectAll);
    }

    /**
     * Hide other fields
     */
    private void hideOtherFields() {
        // Hide other fields
        parent.hideAll();
        // Show this
        getControl().setVisibility(View.VISIBLE);
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
        return fallback;
    }

    /**
     * Save field value
     */
    @Override
    public void save(@NonNull ContentValues values) {
        values.put(getName(), isChecked);
    }

    /*
     * ####### LISTENERS #######
     */

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case button
        if (id == R.id.button) {
            // Case subscribed
            if(isChecked) {
                // Show loading
                parent.showActivityProgress();
                // Get form and show loading
                parent.triggerContentEvent(new GetNewsletterUnSubscribeFormHelper(), GetNewsletterUnSubscribeFormHelper.createBundle(entry.getApiCall()), this);
            }
            // Case un subscribed
            else {
                // Set button and hide other fields
                setButtonState((TextView) view, true);
            }
        }
    }

    @Override
    public void onSubmitDialogForm(String action, ContentValues values) {
        // Show loading
        parent.showActivityProgress();
        // Submit and show loading
        parent.triggerContentEvent(new SubmitFormHelper(), SubmitFormHelper.createBundle(action, values), this);
    }

    /*
     * ####### RESPONSE #######
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Has parent fragment and is visible
        if (!parent.isFragmentUIActive()) {
            return;
        }
        // Hide loading
        parent.hideActivityProgress();
        // Get event type
        EventType type = baseResponse.getEventType();
        // Case get form
        if (type == EventType.GET_NEWSLETTER_UN_SUBSCRIBE_FORM) {
            // Show dialog
            if (parent.hasParentActivity()) {
                // Form
                Form form = (Form) baseResponse.getContentData();
                // Get manager
                FragmentManager manager = parent.getParentActivity().getSupportFragmentManager();
                // Show form dialog
                DialogFormFragment.newInstance(form, this).show(manager, null);
            }
        }
        else if (type == EventType.SUBMIT_FORM) {
            // Super process common success events
            parent.onRequestComplete(baseResponse);
            // Set button
            setButtonState((TextView) getDataControl(), false);
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Has parent fragment and is visible
        if (!parent.isFragmentUIActive()) {
            return;
        }
        // Hide loading
        parent.hideActivityProgress();
        // Super process errors
        parent.onRequestError(baseResponse);
    }

}

