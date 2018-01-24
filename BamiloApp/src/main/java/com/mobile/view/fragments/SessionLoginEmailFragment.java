package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.classes.models.EmarsysEventModel;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.constants.tracking.CategoryConstants;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.EmailHelper;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.GetLoginFormHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.pojo.DynamicForm;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.service.forms.Form;
import com.mobile.service.forms.FormInputType;
import com.mobile.service.objects.checkout.CheckoutStepLogin;
import com.mobile.service.objects.configs.AuthInfo;
import com.mobile.service.objects.customer.Customer;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.LoginHeaderComponent;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to represent the form login via email.
 * @author sergiopereira
 */
public class SessionLoginEmailFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = SessionLoginEmailFragment.class.getSimpleName();

    protected FragmentType nextFragmentType;
    private ViewGroup mFormContainer;
    private Form mForm;
    private DynamicForm mDynamicForm;
    private Bundle mFormSavedState;
    private boolean isInCheckoutProcess;
    private FragmentType mParentFragmentType;

    /**
     * Empty constructor
     */
    public SessionLoginEmailFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.session_login_email_fragment,
                R.string.login_label,
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
        // Get email
        String mCustomerEmail = null;
        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            // Get customer email
            mCustomerEmail = arguments.getString(ConstantsIntentExtra.DATA);
            // Get checkout flag
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.FLAG_1);
            // Force load form if comes from deep link
            nextFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
        }
        // Set initial value
        mFormSavedState = savedInstanceState;
        if (TextUtils.isNotEmpty(mCustomerEmail) && mFormSavedState == null) {
            mFormSavedState = new Bundle();
            mFormSavedState.putString(FormInputType.email.name(), mCustomerEmail);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
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
        // Show authentication info
        AuthInfo authInfo = CountryPersistentConfigs.getAuthInfo(getContext());
        ((LoginHeaderComponent) view.findViewById(R.id.login_component)).showAuthInfo(LoginHeaderComponent.LOGIN, authInfo, null);
        // Get form container
        mFormContainer = (ViewGroup) view.findViewById(R.id.login_email_form_container);
        // Get forgot password
        view.findViewById(R.id.login_email_button_password).setOnClickListener(this);
        // Get continue button
        view.findViewById(R.id.login_email_button_create).setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        // Validate state
        onValidateState();
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
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE STATE");
        // Case rotation save state
        if (mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
        // Save checkout flag
        outState.putBoolean(ConstantsIntentExtra.FLAG_1, isInCheckoutProcess);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
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
            mFormSavedState = new Bundle();
            mDynamicForm.saveFormState(mFormSavedState);
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

    /*
     * ################ LAYOUT ################
     */

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

    /**
     *
     */
    private void loadForm(Form form) {
        // Create form view
        mDynamicForm = FormFactory.create(FormConstants.LOGIN_FORM, getContext(), form);
        // Load saved state
        mDynamicForm.loadSaveFormState(mFormSavedState);
        // Remove all views
        if (mFormContainer.getChildCount() > 0) {
            mFormContainer.removeAllViews();
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
            EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_FAILED,
                Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
            TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
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
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                // Tracking
                EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_SUCCESS,
                        Constants.LOGIN_METHOD_EMAIL, customer.getId(),
                        EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, EmailHelper.getHost(customer.getEmail()),
                                true));
                TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
                // Finish
                getActivity().onBackPressed();
                return;
            case GET_LOGIN_FORM_EVENT:
                mForm = (Form) baseResponse.getContentData();
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
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Case login form
        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            showFragmentErrorRetry();
        }
        // Case login event
        else if (eventType == EventType.LOGIN_EVENT) {
            // Tracking
            EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                    EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
            TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
            // Validate and show errors
            showFragmentContentContainer();
            showFormValidateMessages(mDynamicForm, baseResponse, eventType);
        }
    }

}
