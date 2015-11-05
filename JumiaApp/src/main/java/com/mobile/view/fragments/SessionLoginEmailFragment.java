package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.GetLoginFormHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author sergiopereira
 */
public class SessionLoginEmailFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = SessionLoginEmailFragment.class.getSimpleName();

    private ViewGroup mFormContainer;

    private Form mForm;

    private DynamicForm mDynamicForm;

    protected FragmentType nextFragmentType;

    private Bundle mFormSavedState;

    private String mCustomerEmail;

    private boolean isInCheckoutProcess;

    /**
     * Get new instance
     */
    public static SessionLoginEmailFragment getInstance(Bundle bundle) {
        SessionLoginEmailFragment fragment = new SessionLoginEmailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public SessionLoginEmailFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.session_login_email_fragment,
                R.string.login_label,
                KeyboardState.ADJUST_CONTENT);
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
        // Saved form state
        mFormSavedState = savedInstanceState;
        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            // Get customer email
            mCustomerEmail = arguments.getString(ConstantsIntentExtra.DATA);
            // Get checkout flag
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.FLAG_1);
            // Force load form if comes from deep link
            nextFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
        }
        // Show checkout tab layout
        if(isInCheckoutProcess) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get form container
        mFormContainer = (ViewGroup) view.findViewById(R.id.login_email_form_container);
        // Get forgot password
        view.findViewById(R.id.login_email_button_password).setOnClickListener(this);
        // Get continue button
        view.findViewById(R.id.login_email_button_create).setOnClickListener(this);
        // Validate state
        onValidateState();
    }

    private void onValidateState() {
        // Case form is empty
        if (mForm == null) {
            triggerLoginForm();
        }
        // Case load form
        else {
            loadForm(mForm);
        }
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
        // validate if there was an error related to facebook
        TrackerDelegator.trackPage(TrackingPage.LOGIN_SIGNUP, getLoadTime(), false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Case rotation save state
        if (mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
        // Save data
        outState.putString(ConstantsIntentExtra.DATA, mCustomerEmail);
        // Save checkout flag
        outState.putBoolean(ConstantsIntentExtra.FLAG_1, isInCheckoutProcess);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        // Case goes to back stack save the state
        if(mDynamicForm != null) {
            Bundle bundle = new Bundle();
            mDynamicForm.saveFormState(bundle);
            mFormSavedState = bundle;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     *
     */
    private void loadForm(Form form) {
        // Create form view
        mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getActivity(), form);
        // Load saved state
        mDynamicForm.loadSaveFormState(mFormSavedState);
        // Set initial value
        if (TextUtils.isNotEmpty(mCustomerEmail)) {
            mDynamicForm.setInitialValue(FormInputType.email, mCustomerEmail);
        }
        // Add form view
        mFormContainer.addView(mDynamicForm.getContainer());
        // Show
        showFragmentContentContainer();
    }

     /*
     * ################ LISTENERS ################
     */

    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case sign in button
        if (id == R.id.login_email_button_create) {
            onClickCreateButton();
        }
        // Case forgot password
        else if (id == R.id.login_email_button_password) {
            onClickForgotPassword();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickForgotPassword() {
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    private void onClickCreateButton() {
        // Case valid
        if (mDynamicForm.validate()) {
            requestLogin();
        }
        // Case invalid
        else {
            TrackerDelegator.trackLoginFailed(TrackerDelegator.ISNT_AUTO_LOGIN, GTMValues.LOGIN, GTMValues.EMAILAUTH);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onValidateState();
    }

    /*
     * ################ TRIGGERS ################
     */

    private void requestLogin() {
        getBaseActivity().hideKeyboard();
        triggerLogin(mDynamicForm.save());
    }

    private void triggerLogin(ContentValues values) {
        triggerContentEvent(new LoginHelper(), LoginHelper.createLoginBundle(values), this);
    }

    private void triggerLoginForm() {
        triggerContentEvent(new GetLoginFormHelper(), null, this);
    }

    /*
     * ################ RESPONSES ################
     */

    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS: " + eventType.action);
        switch (eventType) {
            case LOGIN_EVENT:
                // Get customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, false, false);
                // Notify user
                showInfoLoginSuccess();
                // Finish
                getActivity().onBackPressed();
                return;
            case GET_LOGIN_FORM_EVENT:
                mForm = (Form) baseResponse.getMetadata().getData();
                loadForm(mForm);
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate error
        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        // Case login form
        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            showFragmentErrorRetry();
        }
        // Case login event
        else if (eventType == EventType.LOGIN_EVENT) {
            // Tracking
            TrackerDelegator.trackLoginFailed(false, GTMValues.LOGIN, GTMValues.EMAILAUTH);
            // Validate and show errors
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                Print.d(TAG, "SHOW DIALOG");
                Map<String, List<String>> errors = baseResponse.getErrorMessages();
                List<String> errorMessages = null;
                if (errors != null) {
                    errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
                }
                if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                    showFragmentContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.error_login_title),
                            errorMessages.get(0),
                            getString(R.string.ok_label), "", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dismissDialogFragment();
                                    }
                                }
                            });
                    dialog.show(getBaseActivity().getSupportFragmentManager(), null);
                } else {
                    showUnexpectedErrorWarning();
                }
            } else {
                showFragmentErrorRetry();
            }
        }
    }

}
