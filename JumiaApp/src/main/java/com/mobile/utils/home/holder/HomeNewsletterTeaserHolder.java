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
import com.mobile.helpers.SubmitFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.TeaserFormObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

/**
 *
 */
public class HomeNewsletterTeaserHolder extends BaseTeaserViewHolder {

    private final ViewGroup mContainerView;
    private final Button mSubmit;
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
            mNewsLetterForm = FormFactory.getSingleton().create(FormConstants.NEWSLETTER_FORM, mContext,form);
            mContainerView.addView(mNewsLetterForm.getContainer());
            DynamicFormItem item = mNewsLetterForm.getItemByKey(RestConstants.EMAIL);
            if(item.getDataControl() instanceof EditText){
                ((EditText) item.getDataControl()).addTextChangedListener(mTextWatcher);
                ((EditText) item.getDataControl()).setTextColor(ContextCompat.getColor(mContext, R.color.white));

                mSubmit.setEnabled(TextUtils.isNotEmpty(((EditText) item.getDataControl()).getText()));
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
}