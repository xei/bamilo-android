package com.mobile.view.newfragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.EventConstants;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.EventFactory;
import com.mobile.helpers.EmailHelper;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.libraries.emarsys.EmarsysMobileEngage;
import com.mobile.libraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.checkout.CheckoutStepLogin;
import com.mobile.service.objects.customer.Customer;
import com.mobile.service.objects.customer.CustomerEmailCheck;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.gtm.GTMValues;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;
import com.pushwoosh.PushManager;

import java.util.EnumSet;

public class LoginFragment extends NewBaseFragment implements IResponseCallback {
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mLoginErrorMessage, mPasswordErrorMessage;
    private String mCustomerEmail;
    private String mCustomerPassword;
    private long mGABeginRequestMillis;
    private static final String TAG = LoginFragment.class.getSimpleName();

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;


/*    public LoginFragment()
    {

    }*/

    public LoginFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.new_session_login_main_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            // Force load form if comes from deep link
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            mNextStepFromParent = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }


    }


/*    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_session_login_main_fragment, container, false);
        return view;
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmailView = (EditText) view.findViewById(R.id.login_email);
        mPasswordView = (EditText) view.findViewById(R.id.login_password);
        mEmailView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        // Get error message
        mLoginErrorMessage = (TextView) view.findViewById(R.id.login_text_error_message);
        mPasswordErrorMessage = (TextView) view.findViewById(R.id.password_text_error_message);

        // Get continue button
        view.findViewById(R.id.login_button_continue).setOnClickListener(this);
        view.findViewById(R.id.login_email_button_password).setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("mEmailView", mEmailView.getText().toString());
        outState.putString("mPasswordView", mPasswordView.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mEmailView.setText(savedInstanceState.getString("mEmailView"));
            mPasswordView.setText(savedInstanceState.getString("mPasswordView"));
        }
    }

    protected void onClickCheckEmail() {
        Print.i(TAG, "ON CLICK CHECK EMAIL");
        mGABeginRequestMillis = System.currentTimeMillis();

        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        mCustomerPassword = mPasswordView.getText().toString();
        mLoginErrorMessage.setVisibility(View.GONE);
        mPasswordErrorMessage.setVisibility(View.GONE);
        // Trigger to check email
        if (TextUtils.isNotEmpty(mCustomerEmail) && TextUtils.isNotEmpty(mCustomerPassword) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            mLoginErrorMessage.setVisibility(View.GONE);
            mPasswordErrorMessage.setVisibility(View.GONE);
        /*    mEmailView.setError("");
            mPasswordView.setError("");*/

            //mErrorMessage.setVisibility(View.GONE);
        } else {

            if (!TextUtils.isNotEmpty(mCustomerEmail)) {
                mLoginErrorMessage.setText(getResources().getString(R.string.error_ismandatory));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
                //mEmailView.setError(getString(R.string.error_invalid_email));
                mLoginErrorMessage.setText(getResources().getString(R.string.error_invalid_email));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNotEmpty(mCustomerPassword)) {
                // mPasswordView.setError(getString(R.string.error_ismandatory));
                mPasswordErrorMessage.setText(getResources().getString(R.string.error_ismandatory));
                mPasswordErrorMessage.setVisibility(View.VISIBLE);
            }

            //mErrorMessage.setText(getString(R.string.error_invalid_email));
            //mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void triggerEmailCheck(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        triggerContentEventProgress(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case EMAIL_CHECK:
                //DROID-10
                TrackerDelegator.trackScreenLoadTiming(R.string.gaLogin, mGABeginRequestMillis, mCustomerEmail);
                mLoginErrorMessage.setVisibility(View.GONE);
                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                if (exist) {
                    ContentValues values = new ContentValues();
                    values.put("login[email]", mCustomerEmail);
                    values.put("login[password]", mCustomerPassword);
                    triggerContentEventProgress(new LoginHelper(), LoginHelper.createLoginBundle(values), this);

                } else {
                    hideActivityProgress();

                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));

                }

                break;
            case AUTO_LOGIN_EVENT:
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();

                //Emarsys
                EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
                    @Override
                    public void EmarsysMobileEngageResponse(boolean success) {
                    }
                };
                EmarsysMobileEngage.getInstance(getBaseActivity()).sendLogin(PushManager.getPushToken(getBaseActivity()), emarsysMobileEngageResponse);
                // End of Emarsys

                // Case valid next step
                if (nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                    // Tracking
                    if (eventType == EventType.GUEST_LOGIN_EVENT) {
                        TrackerDelegator.storeFirstCustomer(customer);
                        TrackerDelegator.trackSignupSuccessful(GTMValues.CHECKOUT);

                        // Set hide change password
                        CustomerUtils.setChangePasswordVisibility(getBaseActivity(), true);
                    } else if (eventType == EventType.AUTO_LOGIN_EVENT) {
                        TrackerDelegator.trackLoginSuccessful(customer, true, false);
                    } else {
                        TrackerDelegator.trackLoginSuccessful(customer, false, true);
                        // Set hide change password
                        CustomerUtils.setChangePasswordVisibility(getBaseActivity(), true);
                    }
                    // Validate the next step
                    CheckoutStepManager.validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi, getArguments());
                }
                // Case unknown checkout step
                else {
                    hideActivityProgress();
                    // Show layout to call to order
                    showFragmentUnknownCheckoutStepError();
                }
                break;
            case LOGIN_EVENT:
                hideActivityProgress();
                // Get customer
                nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, false, false);

               /* RecommendManager recommendManager = new RecommendManager();
                recommendManager.setEmail(BamiloApplication.CUSTOMER.getEmail(), ""+BamiloApplication.CUSTOMER.getId());*/
                //Emarsys
                emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
                    @Override
                    public void EmarsysMobileEngageResponse(boolean success) {}
                };
                EmarsysMobileEngage.getInstance(getBaseActivity()).sendLogin(PushManager.getPushToken(getBaseActivity()), emarsysMobileEngageResponse);
                // End of Emarsys
                // Finish
                getActivity().onBackPressed();

                PushManager.getInstance(getBaseActivity()).setUserId(getBaseActivity(), BamiloApplication.CUSTOMER.getEmail() + "");
                Crashlytics.setUserEmail(BamiloApplication.CUSTOMER.getEmail());

                TrackerManager.postEvent(getBaseActivity(), EventConstants.Login, EventFactory.login("email", EmailHelper.getHost(customer.getEmail()), true));

                if (isInCheckoutProcess) {
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, null, FragmentController.ADD_TO_BACK_STACK);
                }
                return;
           /* case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupSuccessful(GTMValues.REGISTER);
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                break;*/
            default:
                hideActivityProgress();
                break;
        }

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate error o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        switch (eventType) {
            case EMAIL_CHECK:
                // Show warning
                mLoginErrorMessage.setText(getResources().getString(R.string.error_invalid_email));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
                //showWarningErrorMessage(getString(R.string.error_invalid_email));
                // Show content
                showFragmentContentContainer();
                break;
            case AUTO_LOGIN_EVENT:
                // Logout
                LogOut.perform(getWeakBaseActivity());
            /*case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showValidateMessages(baseResponse);
                break;*/
            case LOGIN_EVENT:
                hideActivityProgress();
                getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));
                TrackerManager.postEvent(getBaseActivity(), EventConstants.Login, EventFactory.login("email", EventConstants.UNKNOWN_EVENT_VALUE, false));
                break;
            default:
                break;
        }
    }

    private void onClickForgotPassword() {
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case forgot password
        if (id == R.id.login_button_continue) {
            onClickCheckEmail();
        } else if (id == R.id.login_email_button_password) {
            onClickForgotPassword();
        }
        /*else if (id == R.id.register_button_create)
        {
            onClickCreate();
        }*/
        // Case super
        else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Print.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }
}