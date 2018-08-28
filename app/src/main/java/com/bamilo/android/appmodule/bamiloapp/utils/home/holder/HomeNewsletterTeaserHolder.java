package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import com.bamilo.android.framework.components.absspinner.IcsSpinner;
import com.bamilo.android.framework.components.customfontviews.Button;
import com.bamilo.android.framework.components.customfontviews.EditText;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.helpers.SubmitFormHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.FormInputType;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.object.TeaserFormObject;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.appmodule.bamiloapp.utils.RadioGroupLayout;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

/**
 * View Holder used to represent the Newsletter Teaser.
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder implements View.OnClickListener, IResponseCallback, TextWatcher, TextView.OnEditorActionListener {

    private final ViewGroup mContainerView;
    private final Button mSubmit;
    private EditText mEditText;
    private RadioGroupLayout mRadioGroupLayout;
    private IcsSpinner mGenderSpinner;
    protected DynamicForm mNewsLetterForm;
    public static String sInitialValue;
    public static int sInitialGender = IntConstants.INVALID_POSITION;

    /**
     * Constructor
     */
    public HomeNewsletterTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get container
        mContainerView = view.findViewById(R.id.home_teaser_newsletter_form_container);
        mSubmit = view.findViewById(R.id.send_newsletter);
        mSubmit.setOnClickListener(this);
        mSubmit.setEnabled(false);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        Form form = null;
        try {
            form = ((TeaserFormObject) group.getData().get(IntConstants.DEFAULT_POSITION)).getForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (form != null) {
            mNewsLetterForm = FormFactory.create(FormConstants.NEWSLETTER_FORM, mContext, form);
            mContainerView.addView(mNewsLetterForm.getContainer());
            for (DynamicFormItem control : mNewsLetterForm) {
                View view = control.getDataControl();
                if (view instanceof EditText) {
                    mEditText = (EditText) view;
                    mEditText.addTextChangedListener(this);
                    mEditText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    mEditText.setHintTextColor(ContextCompat.getColor(mContext, R.color.black_700));
                    if (TextUtils.isNotEmpty(sInitialValue)) {
                        mEditText.setText(sInitialValue);
                    }
                    mEditText.setOnEditorActionListener(this);
                    mSubmit.setEnabled(TextUtils.isNotEmpty(mEditText.getText()));
                }
                // Case RadioGroup: Get Gender choice to save on rotation.
                else if (control.getDataControl() instanceof RelativeLayout &&
                        control.getDataControl().findViewById(R.id.radio_group_container) != null) { // Get Gender choice to save on rotation.
                    mRadioGroupLayout = control.getDataControl().findViewById(R.id.radio_group_container);
                    if (sInitialGender > 0) {
                        mRadioGroupLayout.setSelection(sInitialGender);
                    }
                }
                // Case Spinner: Get the selection
                else if (control.getEntry().getInputType() == FormInputType.list) {
                    mGenderSpinner = (IcsSpinner) control.getDataControl();
                    if (sInitialGender > IntConstants.INVALID_POSITION) {
                        mGenderSpinner.setSelection(sInitialGender);
                    }
                }
            }
        }
    }

    /*
     * ########### LISTENERS ###########
     */

    @Override
    public void onClick(final View v) {
        if (v != null && mParentClickListener != null && mNewsLetterForm != null && validate()) {
            mParentClickListener.onClick(v);
            BamiloApplication.INSTANCE.sendRequest(new SubmitFormHelper(), SubmitFormHelper.createBundle(mNewsLetterForm.getForm().getAction(), mNewsLetterForm.save()), this);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO) {
            InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSubmit.setEnabled(TextUtils.isNotEmpty(s));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /*
     * ########### RESPONSE ###########
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        if (mParentClickListener instanceof IResponseCallback) {
            ((IResponseCallback) mParentClickListener).onRequestComplete(baseResponse);
            UIUtils.hideViewFadeOut(itemView);
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        if(CollectionUtils.isNotEmpty(baseResponse.getValidateMessages())){
            mNewsLetterForm.showValidateMessages(baseResponse.getValidateMessages());
        }
        ((IResponseCallback) mParentClickListener).onRequestError(baseResponse);
    }

    protected boolean validate() {
        boolean result = true;
        for (DynamicFormItem control : mNewsLetterForm) {
            result &= control.validate();
        }
        return result;
    }

    /*
     * ########### SAVE STATE ###########
     */

    public String getEditedText() {
        return sInitialValue = mEditText != null ? mEditText.getText().toString() : null;
    }

    public int getSelectedGender() {
        if (mRadioGroupLayout != null) {
            return sInitialGender = mRadioGroupLayout.getSelectedIndex();
        } else if (mGenderSpinner != null) {
            return sInitialGender = mGenderSpinner.getSelectedItemPosition();
        }
        return IntConstants.INVALID_POSITION;
    }
}