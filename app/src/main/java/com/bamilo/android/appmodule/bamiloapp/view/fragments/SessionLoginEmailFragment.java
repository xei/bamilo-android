package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.GetLoginFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.FormInputType;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.configs.AuthInfo;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.LoginHeaderComponent;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.EnumSet;
import java.util.Objects;

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Show authentication info
        AuthInfo authInfo = CountryPersistentConfigs.getAuthInfo(getContext());
        ((LoginHeaderComponent) view.findViewById(R.id.login_component)).showAuthInfo(LoginHeaderComponent.LOGIN, authInfo, null);
        // Get form container
        mFormContainer = view.findViewById(R.id.login_email_form_container);
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
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
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
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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
//        else {
//            MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_FAILED,
//                Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
//                MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
//            TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
//
//            EmarsysTracker.getInstance().trackEventAppLogin(Integer.parseInt(getContext().getResources().getString(R.string.Emarsys_ContactFieldID)),BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);
//
//        }
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
        triggerContentEvent(new LoginHelper(getContext()), LoginHelper.createLoginBundle(values), this);
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
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case LOGIN_EVENT:
                // Get customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);



                EventTracker.INSTANCE.login(
                        String.valueOf(customer.getId()),
                        String.valueOf(customer.getEmail()),
                        String.valueOf(customer.getPhoneNumber()),
                        TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL);


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
            return;
        }
        // Super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate messageItem
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        // Case login form
        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            showFragmentErrorRetry();
        }
        // Case login event
        else if (eventType == EventType.LOGIN_EVENT) {
            // Tracking
//            MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_FAILED,
//                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
//                    MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
//            TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
            String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : null;
            String emailAddress = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null;
            String phoneNumber = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getPhoneNumber() : null;
            EventTracker.INSTANCE.login(userId, emailAddress, phoneNumber, TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL);

//            EmarsysTracker.getInstance().trackEventAppLogin(Integer.parseInt(getContext().getResources().getString(R.string.Emarsys_ContactFieldID)),BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);


            // Validate and show errors
            showFragmentContentContainer();
            showFormValidateMessages(mDynamicForm, baseResponse, eventType);
        }
    }

}
