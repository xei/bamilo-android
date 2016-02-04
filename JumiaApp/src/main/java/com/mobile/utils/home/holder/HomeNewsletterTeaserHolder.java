package com.mobile.utils.home.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.FormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.TeaserFormObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.HashMap;

/**
 *
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();
    public static final String SHOW_LOADING = "show_loading";
    public static final String HIDE_LOADING = "hide_loading";

    public ViewGroup mContainerView;
    public Button mSubmit;
    protected DynamicForm mNewsLetterForm;
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
            mNewsLetterForm = FormFactory.getSingleton().CreateForm(FormConstants.NEWSLETTER_FORM, mContext,form);
            mContainerView.addView(mNewsLetterForm.getContainer());
            DynamicFormItem item = mNewsLetterForm.getItemByKey(RestConstants.EMAIL);
            if(item.getEditControl() instanceof EditText){
                ((EditText) item.getEditControl()).addTextChangedListener(mTextWatcher);
                ((EditText) item.getEditControl()).setTextColor(ContextCompat.getColor(mContext, R.color.white));
                if(TextUtils.isNotEmpty(((EditText) item.getEditControl()).getText())){
                    mSubmit.setEnabled(true);
                } else {
                    mSubmit.setEnabled(false);
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
            if(TextUtils.isEmpty(s)){
                mSubmit.setEnabled(false);
            } else {
                mSubmit.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(mNewsLetterForm.validate()){
                v.setTag(SHOW_LOADING);
                mParentClickListener.onClick(v);
                JumiaApplication.INSTANCE.sendRequest(new FormHelper(), FormHelper.createBundle(mNewsLetterForm.getForm().getAction(), mNewsLetterForm.save()), new IResponseCallback() {
                    @Override
                    public void onRequestComplete(BaseResponse baseResponse) {

                        v.setTag(baseResponse);
                        mParentClickListener.onClick(v);
                        UIUtils.hideViewFadeOut(itemView);
                    }
                    @Override
                    public void onRequestError(BaseResponse baseResponse) {
                        if(baseResponse.getErrorMessages() != null && baseResponse.getErrorMessages().containsKey("ALREADY_SUBSCRIBED")){
                            UIUtils.hideViewFadeOut(itemView);
                            baseResponse.setSuccess(true);
                            baseResponse.setSuccessMessages((HashMap<String, String>) baseResponse.getErrorMessages());
                        }
                        v.setTag(baseResponse);
                        mParentClickListener.onClick(v);
                    }
                });

            }
        }
    };
}