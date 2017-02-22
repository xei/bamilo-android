package com.mobile.view.newfragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.FacebookTextView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.LoginAutoHelper;
import com.mobile.helpers.session.LoginFacebookHelper;
import com.mobile.helpers.session.LoginGuestHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.configs.AuthInfo;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.LoginHeaderComponent;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 */
public class NewSessionLoginMainFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewSessionLoginMainFragment.class.getSimpleName();

    private EditText mEmailView;
    private EditText mPasswordView;

    private EditText mNationalIdView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailRView;
    private EditText mPasswordRView;
    private EditText mPhoneView;

    private String mCustomerEmail;
    private String mCustomerPassword;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    private TextView mLoginErrorMessage , mPasswordErrorMessage;
    private TextView national_id_error_message,first_name_error_message,last_name_error_message,email_error_message,password_error_message,phone_error_message;
    TabLayout.Tab tabLogin;
    TabLayout.Tab tabRegister;
    //DROID-10
    private long mGABeginRequestMillis;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    /**
     * Empty constructor
     */
    public NewSessionLoginMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.new_session_main_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
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



        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.login_tabs);
        tabLogin = tabLayout.newTab();
        tabLogin.setTag("Login");
        tabLayout.addTab(tabLogin);
        tabLogin.setText(R.string.login_label);


        // Saved
        tabRegister = tabLayout.newTab();
        tabLayout.addTab(tabRegister);
        tabRegister.setTag("Register");
        tabRegister.setText(R.string.register_label);
        //tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag().toString().compareTo("Login")==0) {
                    getFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
                }
                if (tab.getTag().toString().compareTo("Register")==0) {
                    getFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabRegister.select();
        tabLogin.select();
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
        // Tracking
        TrackerDelegator.trackPage(TrackingPage.LOGIN_SIGN_UP, getLoadTime(), false);

        // Case auto login
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            triggerAutoLogin();
        }
        // Case show content
        else {
            Print.i(TAG, "USER WITHOUT CREDENTIALS");
            // Show content
            showFragmentContentContainer();
        }
    }



    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
        outState.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, mNextStepFromParent);
        outState.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, isInCheckoutProcess);
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
        getBaseActivity().hideKeyboard();
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
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");

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
     * ################ LISTENERS ################
     */



     /*
     * ################ LISTENERS ################
     */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case forgot password
        if (id == R.id.login_button_continue) {
            onClickCheckEmail();
        }
        else if (id == R.id.login_email_button_password) {
            onClickForgotPassword();
        }
        else if (id == R.id.register_button_create)
        {
            onClickCreate();
        }
        // Case super
        else {
            super.onClick(view);
        }
    }

    private void onClickForgotPassword() {
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    private boolean checkValidation()
    {
        Context context = getBaseActivity();

        boolean result = validateStringToPattern(context, R.string.national_id, mNationalIdView, mNationalIdView.getText().toString(), true, 10, 10, R.string.normal_string_regex, "");
        result = validateStringToPattern(context, R.string.first_name, mFirstNameView, mFirstNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "") && result;
        result = validateStringToPattern(context, R.string.last_name, mLastNameView, mLastNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "") && result;
        result = validateStringToPattern(context, R.string.email, mEmailRView, mEmailRView.getText().toString(), true, 0, 40, R.string.email_regex, getResources().getString(R.string.error_invalid_email)) && result;
        result = validateStringToPattern(context, R.string.new_password, mPasswordRView, mPasswordRView.getText().toString(), true, 6, 50, R.string.normal_string_regex, "") && result;
        result = validateStringToPattern(context, R.string.phone, mPhoneView, mPhoneView.getText().toString(), true, 0, 40, R.string.normal_string_regex, "") && result;
        return result;
    }
    void requestRegister() {



        // Get values
        ContentValues values = new ContentValues();
        values.put("customer[national_id]", mNationalIdView.getText().toString());
        values.put("customer[first_name]", mFirstNameView.getText().toString());
        values.put("customer[last_name]", mLastNameView.getText().toString());
        values.put("customer[email]", mEmailRView.getText().toString());
        values.put("customer[password]", mPasswordRView.getText().toString());
        values.put("customer[phone]", mPhoneView.getText().toString());
        values.put("customer[phone_prefix]", "100");

        // Register user
        triggerRegister("form_submit::customer/create/", values);
    }

    private void onClickCreate() {
        // Hide keyboard
        getBaseActivity().hideKeyboard();
        // Case valid
        if (checkValidation()) {
            requestRegister();
        }
        // Case invalid
        else {
            // Tracking
            TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
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
        if(TextUtils.isNotEmpty(mCustomerEmail) && TextUtils.isNotEmpty(mCustomerPassword) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            mLoginErrorMessage.setVisibility(View.GONE);
            mPasswordErrorMessage.setVisibility(View.GONE);
        /*    mEmailView.setError("");
            mPasswordView.setError("");*/

            //mErrorMessage.setVisibility(View.GONE);
        } else {

            if (!TextUtils.isNotEmpty(mCustomerEmail)){
                mLoginErrorMessage.setText(getResources().getString(R.string.error_ismandatory));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
            }
            else if( !Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches())
            {
                //mEmailView.setError(getString(R.string.error_invalid_email));
                mLoginErrorMessage.setText(getResources().getString(R.string.error_invalid_email));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNotEmpty(mCustomerPassword))
            {
               // mPasswordView.setError(getString(R.string.error_ismandatory));
                mPasswordErrorMessage.setText(getResources().getString(R.string.error_ismandatory));
                mPasswordErrorMessage.setVisibility(View.VISIBLE);
            }

            //mErrorMessage.setText(getString(R.string.error_invalid_email));
            //mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Print.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }

    /*
     * ################ TRIGGERS ################
     */

    private void triggerEmailCheck(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        triggerContentEventProgress(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    private void triggerAutoLogin() {
        Print.i(TAG, "TRIGGER AUTO LOGIN");
        triggerContentEventProgress(new LoginAutoHelper(), LoginAutoHelper.createAutoLoginBundle(), this);
    }

    private void triggerRegister(String endpoint, ContentValues values) {
        triggerContentEventProgress(new RegisterHelper(), RegisterHelper.createBundle(endpoint, values), this);
    }

    /*
     * ################ RESPONSE ################
     */

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
                hideActivityProgress();
                mLoginErrorMessage.setVisibility(View.GONE);
                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                if (exist)
                {
                    ContentValues values = new ContentValues();
                    values.put("login[email]", mCustomerEmail);
                    values.put("login[password]", mCustomerPassword);
                    triggerContentEventProgress(new LoginHelper(), LoginHelper.createLoginBundle(values), this);

                }
                else
                {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));

                }

                break;
            case AUTO_LOGIN_EVENT:
                hideActivityProgress();
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();
                // Case valid next step
                if(nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                    // Tracking
                    if (eventType == EventType.GUEST_LOGIN_EVENT) {
                        TrackerDelegator.storeFirstCustomer(customer);
                        TrackerDelegator.trackSignupSuccessful(GTMValues.CHECKOUT);
                        // Set hide change password
                        CustomerUtils.setChangePasswordVisibility(getBaseActivity(),true);
                    } else if (eventType == EventType.AUTO_LOGIN_EVENT) {
                        TrackerDelegator.trackLoginSuccessful(customer, true, false);
                    } else {
                        TrackerDelegator.trackLoginSuccessful(customer, false, true);
                        // Set hide change password
                        CustomerUtils.setChangePasswordVisibility(getBaseActivity(),true);
                    }
                    // Validate the next step
                    CheckoutStepManager.validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi, getArguments());

                }
                // Case unknown checkout step
                else {
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
                // Finish
                getActivity().onBackPressed();
                if (isInCheckoutProcess)
                {
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, null, FragmentController.ADD_TO_BACK_STACK);
                }
                return;
            case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupSuccessful(GTMValues.REGISTER);
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                break;
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
            case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showValidateMessages(baseResponse);
                break;
            case LOGIN_EVENT:
                hideActivityProgress();
                getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));
                break;
            default:
                break;
        }
    }

    private void showValidateMessages(BaseResponse baseResponse) {
        Map map = baseResponse.getValidateMessages();
        national_id_error_message.setVisibility(View.GONE);
        first_name_error_message.setVisibility(View.GONE);
        last_name_error_message.setVisibility(View.GONE);
        email_error_message.setVisibility(View.GONE);
        password_error_message.setVisibility(View.GONE);
        phone_error_message.setVisibility(View.GONE);

        if (CollectionUtils.isNotEmpty(map)) {
            for (Object key : map.keySet()) {
                switch (key.toString())
                {
                    case "national_id":
                        national_id_error_message.setVisibility(View.VISIBLE);
                        national_id_error_message.setText("5151515"+map.get(key).toString());
                        break;
                    case "first_name":
                        first_name_error_message.setVisibility(View.VISIBLE);
                        first_name_error_message.setText(map.get(key).toString());
                        break;
                    case "last_name":
                        last_name_error_message.setVisibility(View.VISIBLE);
                        last_name_error_message.setText(map.get(key).toString());
                        break;
                    case "email":
                        email_error_message.setVisibility(View.VISIBLE);
                        email_error_message.setText(map.get(key).toString());
                        break;
                    case "password":
                        password_error_message.setVisibility(View.VISIBLE);
                        password_error_message.setText(map.get(key).toString());
                        break;
                    case "phone":
                        phone_error_message.setVisibility(View.VISIBLE);
                        phone_error_message.setText(map.get(key).toString());
                        break;
                }

            }
        }
    }


    @SuppressLint("ValidFragment")
    class LoginFragment extends Fragment
    {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.new_session_login_main_fragment, container, false);
            return view;
        }

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
            view.findViewById(R.id.login_button_continue).setOnClickListener(NewSessionLoginMainFragment.this);
            view.findViewById(R.id.login_email_button_password).setOnClickListener(NewSessionLoginMainFragment.this);
        }

    }

    @SuppressLint("ValidFragment")
    class RegisterFragment extends Fragment
    {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.new_session_register_main_fragment, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNationalIdView = (EditText) view.findViewById(R.id.national_id);
            mFirstNameView = (EditText) view.findViewById(R.id.first_name);
            mLastNameView = (EditText) view.findViewById(R.id.last_name);
            mEmailRView = (EditText) view.findViewById(R.id.email);
            mPasswordRView = (EditText) view.findViewById(R.id.password);
            mPhoneView = (EditText) view.findViewById(R.id.phone);
            phone_error_message = (TextView) view.findViewById(R.id.phone_error_message);
            national_id_error_message = (TextView) view.findViewById(R.id.national_id_error_message);
            first_name_error_message = (TextView) view.findViewById(R.id.first_name_error_message);
            last_name_error_message = (TextView) view.findViewById(R.id.last_name_error_message);
            email_error_message = (TextView) view.findViewById(R.id.email_error_message);
            password_error_message = (TextView) view.findViewById(R.id.password_error_message);
            phone_error_message = (TextView) view.findViewById(R.id.phone_error_message);
            view.findViewById(R.id.register_button_create).setOnClickListener(NewSessionLoginMainFragment.this);
            mEmailRView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
    }

}
