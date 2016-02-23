package com.mobile.utils.home.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.SubmitFormHelper;
import com.mobile.helpers.search.GetSearchSuggestionsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.TeaserFormObject;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

/**
 *
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = HomeNewsletterTeaserHolder.class.getName();
    private final ViewGroup mContainerView;
    private final Button mSubmit;
    private EditText mEditText;
    private RadioGroupLayout mRadioGroupLayout;
    protected DynamicForm mNewsLetterForm;
    public static String mInitialValue;
    public static int mInitialGender = -1;

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
                    if(TextUtils.isNotEmpty(mInitialValue)) {
                        mEditText.setText(mInitialValue);
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
                    if(mInitialGender > 0 ){
                        mRadioGroupLayout.setSelection(mInitialGender);
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
            if(v != null && mParentClickListener != null && mNewsLetterForm != null && mNewsLetterForm.validate()){
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

    public String getEditedText(){
        return mInitialValue = mEditText.getText().toString();
    }
    public int getSelectedGender(){
        return mInitialGender = mRadioGroupLayout.getSelectedIndex();
    }
}