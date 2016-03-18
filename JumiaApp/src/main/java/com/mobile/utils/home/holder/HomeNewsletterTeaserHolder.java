package com.mobile.utils.home.holder;

import android.content.Context;
import android.support.annotation.Nullable;
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

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.SubmitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.TeaserFormObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

/**
 * View Holder used to represent the Newsletter Teaser.
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder implements View.OnClickListener, IResponseCallback, TextWatcher, TextView.OnEditorActionListener {

    private final ViewGroup mContainerView;
    private final Button mSubmit;
    private EditText mEditText;
    private RadioGroupLayout mRadioGroupLayout;
    protected DynamicForm mNewsLetterForm;
    public static String sInitialValue;
    public static int sInitialGender = IntConstants.INVALID_POSITION;

    /**
     * Constructor
     */
    public HomeNewsletterTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get container
        mContainerView = (ViewGroup) view.findViewById(R.id.home_teaser_newsletter_form_container);
        mSubmit = (Button) view.findViewById(R.id.send_newsletter);
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
            form.setType(FormConstants.NEWSLETTER_FORM);
            mNewsLetterForm = FormFactory.getSingleton().create(FormConstants.NEWSLETTER_FORM, mContext, form);
            mContainerView.addView(mNewsLetterForm.getContainer());
            for (DynamicFormItem control : mNewsLetterForm) {
                View view = control.getDataControl();
                if (view instanceof EditText) {
                    mEditText = (EditText) view;
                    mEditText.addTextChangedListener(this);
                    mEditText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    if (TextUtils.isNotEmpty(sInitialValue)) {
                        mEditText.setText(sInitialValue);
                    }
                    mEditText.setOnEditorActionListener(this);
                    mSubmit.setEnabled(TextUtils.isNotEmpty(mEditText.getText()));
                }
                // Get Gender choice to save on rotation.
                else if (view instanceof RelativeLayout) {
                    mRadioGroupLayout = (RadioGroupLayout) view.findViewById(R.id.radio_group_container);
                    if (mRadioGroupLayout != null && sInitialGender > 0) {
                        mRadioGroupLayout.setSelection(sInitialGender);
                    }
                }
            }
        }
    }

    /*
     * ########### SAVE STATE ###########
     */

    @Nullable
    public String getEditedText() {
        return sInitialValue = mEditText != null ? mEditText.getText().toString() : null;
    }

    public int getSelectedGender() {
        return sInitialGender = mRadioGroupLayout != null ? mRadioGroupLayout.getSelectedIndex() : IntConstants.INVALID_POSITION;
    }

    /*
     * ########### LISTENERS ###########
     */

    @Override
    public void onClick(final View v) {
        if (v != null && mParentClickListener != null && mNewsLetterForm != null && mNewsLetterForm.validate()) {
            mParentClickListener.onClick(v);
            JumiaApplication.INSTANCE.sendRequest(new SubmitFormHelper(), SubmitFormHelper.createBundle(mNewsLetterForm.getForm().getAction(), mNewsLetterForm.save()), this);
        }
    }

    @Override
    public boolean onEditorAction(android.widget.TextView textView, int actionId, KeyEvent event) {
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
        ((IResponseCallback) mParentClickListener).onRequestError(baseResponse);
    }
}