/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.forms.Form;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.session.GetForgotPasswordFormHelper;
import com.mobile.helpers.session.GetForgotPasswordHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionForgotPasswordFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(SessionForgotPasswordFragment.class);

    protected DynamicForm dynamicForm;

    private LinearLayout container;

    private Form formResponse;

    private Bundle savedInstanceState;

    /**
     * 
     * @return
     */
    public static SessionForgotPasswordFragment getInstance() {
        return new SessionForgotPasswordFragment();
    }

    /**
     * Empty constructor
     */
    public SessionForgotPasswordFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.ForgotPassword,
                R.layout.forgotpassword,
                R.string.forgotpass_header,
                KeyboardState.ADJUST_CONTENT);

        this.setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        dynamicForm = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            if (formResponse != null) displayForm(formResponse);
            else triggerForgotForm();
        } else {
            showFragmentErrorRetry();
        }
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
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
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
        Button buttons = (Button) getView().findViewById(R.id.submit_button);
        buttons.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.submit_button) {
                    if (dynamicForm.validate()) {
                        requestPassword();
                    }
                }
            }
        });
    }

    /**
     * 
     */
    private void requestPassword() {
        ContentValues values = dynamicForm.save();
        triggerForgot(values);
    }

    /**
     * 
     */
    private void displayForm(Form form) {
        Log.d(TAG, "DISPLAY FORM");
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = dynamicForm.getItemByKey("email");
        if (item == null)
            return;
        if (item.getEditControl() != null) {
            ((EditText) item.getEditControl()).setHint(getString(R.string.forgotten_password_examplemail));
        }
        if (getView() == null) {
            Log.e(TAG, "NO VIEW - SWITCHING TO HOME");
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

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        showFragmentContentContainer();
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        switch (eventType) {
        case INIT_FORMS:
        case GET_FORGET_PASSWORD_FORM_EVENT:
            Log.d(TAG, "FORGET_PASSWORD_FORM");
            Form form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (null != form) {
                this.formResponse = form;
                displayForm(form);
            }
            break;
        case FORGET_PASSWORD_EVENT:
            Log.i(TAG, "FORGET_PASSWORD_EVENT successful");
            dialog = DialogGenericFragment.newInstance(
                    true, false,
                    getString(R.string.forgotten_password_resulttitle),
                    getString(R.string.forgotten_password_successtext), 
                    getString(R.string.ok_label), 
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getActivity().getSupportFragmentManager(), null);
            break;
        default:
            break;
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON ERROR EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (super.handleErrorEvent(bundle)) {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (eventType == EventType.FORGET_PASSWORD_EVENT) {
            Log.d(TAG, "FORGET_PASSWORD_EVENT");

            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            List<String> errorMessages = null;
            if (errors != null) {
                errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
            }
            if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                showFragmentContentContainer();
                dialog = DialogGenericFragment.newInstance(true, false,
                        getString(R.string.error_forgotpassword_title),
                        errorMessages.get(0),
                        getString(R.string.ok_label),
                        "",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dismissDialogFragment();
                                }
                            }
                        });
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return true;
            }
        }
        return false;
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerForgotForm() {
        triggerContentEvent(new GetForgotPasswordFormHelper(), null, mCallBack);
    }

    private void triggerForgot(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetForgotPasswordHelper.CONTENT_VALUES, values);
        triggerContentEvent(new GetForgotPasswordHelper(), bundle, mCallBack);
        getBaseActivity().hideKeyboard();
    }

    /**
     * CALLBACK
     * 
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

}