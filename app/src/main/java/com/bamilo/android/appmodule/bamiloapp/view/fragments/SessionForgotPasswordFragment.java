package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.GetForgotPasswordFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.SetForgotPasswordHelper;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.framework.components.customfontviews.EditText;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicFormItem;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * @author sergiopereira
 * 
 */
public class SessionForgotPasswordFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = SessionForgotPasswordFragment.class.getSimpleName();

    protected DynamicForm mDynamicForm;

    private LinearLayout container;

    private Form formResponse;

    private Bundle savedInstanceState;

    private View mButton;

    /**
     * Empty constructor
     */
    public SessionForgotPasswordFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.forgotpassword,
                R.string.forgotpass_header,
                ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TrackerDelegator.trackPage(TrackingPage.FORGOT_PASSWORD, getLoadTime(), false);

        mDynamicForm = null;

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.FORGOT_PASSWORD.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get button
        mButton = view.findViewById(R.id.submit_button);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (formResponse != null) displayForm(formResponse);
        else triggerForgotForm();
        setAppContentLayout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mDynamicForm) {
            mDynamicForm.saveFormState(outState);
            savedInstanceState = outState;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        if (container != null) {
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Inflates the required layout for this activity into the main activity template
     */
    private void setAppContentLayout() {
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDynamicForm.validate()) {
                    requestPassword();
                }
            }
        });
    }

    /**
     * 
     */
    private void requestPassword() {
        triggerForgot(mDynamicForm.getForm().getAction(), mDynamicForm.save());
    }

    /**
     * 
     */
    private void displayForm(Form form) {
        mDynamicForm = FormFactory.create(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = mDynamicForm.getItemByKey(RestConstants.EMAIL);
        if (item == null)
            return;
        if (item.getDataControl() != null) {
            ((EditText) item.getDataControl()).setHint(getString(R.string.forgotten_password_examplemail));
        }
        if (getView() == null) {
            restartAllFragments();
            return;
        }
        container = (LinearLayout) getView().findViewById(R.id.form_container);
        container.addView(mDynamicForm.getContainer());

        // Show save state
        if (null != this.savedInstanceState && null != mDynamicForm) {
            mDynamicForm.loadSaveFormState(savedInstanceState);
        }
    }



    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerForgotForm() {
        triggerContentEvent(new GetForgotPasswordFormHelper(), null, this);
    }

    private void triggerForgot(String action, ContentValues values) {
        triggerContentEvent(new SetForgotPasswordHelper(), SetForgotPasswordHelper.createBundle(action, values), this);
        getBaseActivity().hideKeyboard();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Show container
        showFragmentContentContainer();
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_FORGET_PASSWORD_FORM_EVENT:
                Form form = (Form) baseResponse.getContentData();
                if (null != form) {
                    this.formResponse = form;
                    displayForm(form);

                } else {
                    showFragmentErrorRetry();
                }
                break;
            default:
                getBaseActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Call super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate type
        showFragmentContentContainer();
        EventType eventType = baseResponse.getEventType();
        if (eventType == EventType.FORGET_PASSWORD_EVENT) {
            showFormValidateMessages(mDynamicForm, baseResponse, eventType);
        }
    }
}