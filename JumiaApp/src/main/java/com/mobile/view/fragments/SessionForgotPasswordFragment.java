/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.session.GetForgotPasswordFormHelper;
import com.mobile.helpers.session.SetForgotPasswordHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * @author sergiopereira
 * 
 */
public class SessionForgotPasswordFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = SessionForgotPasswordFragment.class.getSimpleName();

    protected DynamicForm dynamicForm;

    private LinearLayout container;

    private Form formResponse;

    private Bundle savedInstanceState;

    private View mButton;

    /**
     * 
     */
    public static SessionForgotPasswordFragment getInstance() {
        return new SessionForgotPasswordFragment();
    }

    /**
     * Empty constructor
     */
    public SessionForgotPasswordFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.FORGOT_PASSWORD,
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
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        dynamicForm = null;
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
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
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
        if (null != dynamicForm) {
            for (DynamicFormItem item : dynamicForm) {
                item.saveState(outState);
            }
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
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
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
                if (dynamicForm.validate()) {
                    requestPassword();
                }
            }
        });
    }

    /**
     * 
     */
    private void requestPassword() {
        triggerForgot(dynamicForm.save());
    }

    /**
     * 
     */
    private void displayForm(Form form) {
        Print.d(TAG, "DISPLAY FORM");
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = dynamicForm.getItemByKey("email");
        if (item == null)
            return;
        if (item.getEditControl() != null) {
            ((EditText) item.getEditControl()).setHint(getString(R.string.forgotten_password_examplemail));
        }
        if (getView() == null) {
            Print.e(TAG, "NO VIEW - SWITCHING TO HOME");
            restartAllFragments();
            return;
        }
        container = (LinearLayout) getView().findViewById(R.id.form_container);
        container.addView(dynamicForm.getContainer());

        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem dItem = iter.next();
                dItem.loadState(savedInstanceState);
            }
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

    private void triggerForgot(ContentValues values) {
        triggerContentEvent(new SetForgotPasswordHelper(), SetForgotPasswordHelper.createBundle(values), this);
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
        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        showFragmentContentContainer();
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "onSuccessEvent eventType : " + eventType);
        switch (eventType) {
            case INIT_FORMS:
            case GET_FORGET_PASSWORD_FORM_EVENT:
                Print.d(TAG, "FORGET_PASSWORD_FORM");
                Form form = (Form) baseResponse.getContentData();
                if (null != form) {
                    this.formResponse = form;
                    displayForm(form);
                } else {
                    showFragmentErrorRetry();
                }
                break;
            case FORGET_PASSWORD_EVENT:
                Print.i(TAG, "FORGET_PASSWORD_EVENT successful");
                break;
            default:
                break;
        }
        super.handleErrorMessage(baseResponse.getErrorMessage(), baseResponse.getEventTask(), eventType);

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.d(TAG, "ON ERROR EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        showFragmentContentContainer();
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "onErrorEvent: type = " + eventType);
        if (eventType == EventType.FORGET_PASSWORD_EVENT) {
            Print.d(TAG, "FORGET_PASSWORD_EVENT");
            showWarningErrorMessage(baseResponse.getValidateMessage(), EventType.FORGET_PASSWORD_EVENT);
        }

    }
}