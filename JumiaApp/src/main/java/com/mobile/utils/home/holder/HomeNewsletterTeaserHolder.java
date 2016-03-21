package com.mobile.utils.home.holder;

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

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.SubmitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
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
 *
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder {

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
        mContainerView = (ViewGroup) view.findViewById(R.id.home_teaser_newsletter_form_container);
        mSubmit = (Button) view.findViewById(R.id.send_newsletter);
        mSubmit.setOnClickListener(mOnClickListener);
        mSubmit.setEnabled(false);

    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        Form form = null;
        try{
            form = ((TeaserFormObject) group.getData().get(0)).getForm();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(form != null){
            form.setType(FormConstants.NEWSLETTER_FORM);
            mNewsLetterForm = FormFactory.getSingleton().create(FormConstants.NEWSLETTER_FORM, mContext,form);
            mContainerView.addView(mNewsLetterForm.getContainer());
            for (DynamicFormItem control : mNewsLetterForm) {
                if(control.getDataControl() instanceof EditText ){
                    mEditText = (EditText) control.getDataControl();
                    ((EditText) control.getDataControl()).addTextChangedListener(mTextWatcher);
                    ((EditText) control.getDataControl()).setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    if(TextUtils.isNotEmpty(sInitialValue)) {
                        mEditText.setText(sInitialValue);
                    }
                    mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(android.widget.TextView textView, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO) {

                                InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                                return true;
                            }
                            return false;
                        }
                    });

                    mSubmit.setEnabled(TextUtils.isNotEmpty(((EditText) control.getDataControl()).getText()));
                } else if(control.getDataControl() instanceof RelativeLayout &&
                        control.getDataControl().findViewById(R.id.radio_group_container) != null){ // Get Gender choice to save on rotation.
                    mRadioGroupLayout = (RadioGroupLayout) control.getDataControl().findViewById(R.id.radio_group_container);
                    if(sInitialGender > 0 ){
                        mRadioGroupLayout.setSelection(sInitialGender);
                    }
                } else if(control.getEntry().getInputType() == FormInputType.list){
                    mGenderSpinner = (IcsSpinner) control.getDataControl();
                    if(sInitialGender > 0 ) {
                        mGenderSpinner.setSelection(sInitialGender);
                    }
                }
            }

        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
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
    };

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(v != null && mParentClickListener != null && mNewsLetterForm != null && validate()){
                mParentClickListener.onClick(v);
                JumiaApplication.INSTANCE.sendRequest(new SubmitFormHelper(), SubmitFormHelper.createBundle(mNewsLetterForm.getForm().getAction(), mNewsLetterForm.save()), new IResponseCallback() {
                    @Override
                    public void onRequestComplete(BaseResponse baseResponse) {
                        if(mParentClickListener instanceof IResponseCallback){
                            ((IResponseCallback) mParentClickListener).onRequestComplete(baseResponse);
                            UIUtils.hideViewFadeOut(itemView);
                        }

                    }
                    @Override
                    public void onRequestError(BaseResponse baseResponse) {
                        ((IResponseCallback) mParentClickListener).onRequestError(baseResponse);
                    }
                });

            }
        }
    };

    protected boolean validate(){
        boolean result = true;
        for (DynamicFormItem control : mNewsLetterForm) {
            if(control.getEntry().getInputType() == FormInputType.list){
                if(TextUtils.equals(control.getEntry().getPlaceHolder(), (String) ((IcsSpinner) control.getDataControl()).getSelectedItem())){
                    control.showErrorMessage(mContext.getString(R.string.please_fill_all_data));
                   return false;
                }
            }

            result &= control.validate();
        }

        return result;
    }

    public String getEditedText(){
        return sInitialValue = mEditText.getText().toString();
    }
    public int getSelectedGender(){
        if(mRadioGroupLayout != null){
            return sInitialGender = mRadioGroupLayout.getSelectedIndex();
        } else if(mGenderSpinner != null){
            return sInitialGender = mGenderSpinner.getSelectedItemPosition();
        }
        return 0;
    }
}