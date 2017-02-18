package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.configs.AuthInfo;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.LoginHeaderComponent;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 */
public class NewSessionLoginMainFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewSessionLoginMainFragment.class.getSimpleName();

    private EditText mEmailView;
    private EditText mPasswordView;

    private String mCustomerEmail;
    private String mCustomerPassword;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    private TextView mErrorMessage;

    //DROID-10
    private long mGABeginRequestMillis;
    private LoginFragment loginFragment;

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

        loginFragment = new LoginFragment();
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
        // Get info
        /*String text = String.format(getString(R.string.login_main_info), getString(R.string.app_name));
        final LoginHeaderComponent loginHeaderComponent = (LoginHeaderComponent) view.findViewById(R.id.login_component);
        loginHeaderComponent.setSubTitle(text);

        AuthInfo authInfo = CountryPersistentConfigs.getAuthInfo(getContext());
        loginHeaderComponent.showAuthInfo(LoginHeaderComponent.CHECK_EMAIL, authInfo, text);
        View divider = view.findViewById(R.id.login_divider);*/

        // Get email

        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        // Get and set guest button
        //setGuestButton(view.findViewById(R.id.login_button_guest), isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT);
        //setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.login_tabs);
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setTag("Login");
        tabLayout.addTab(tab);
        tab.setText(R.string.login_label);


        // Saved
        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);
        tab2.setTag("Register");
        tab2.setText(R.string.register_label);
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
                            .beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tab2.select();
        tab.select();
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
        // Case super
        else {
            super.onClick(view);
        }
    }


    protected void onClickCheckEmail() {
        Print.i(TAG, "ON CLICK CHECK EMAIL");
        mGABeginRequestMillis = System.currentTimeMillis();

        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        mCustomerPassword = mPasswordView.getText().toString();
        // Trigger to check email
        if(TextUtils.isNotEmpty(mCustomerEmail) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            mErrorMessage.setVisibility(View.GONE);
        } else {
            mErrorMessage.setText(getString(R.string.error_invalid_email));
            mErrorMessage.setVisibility(View.VISIBLE);
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
        triggerContentEvent(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    private void triggerAutoLogin() {
        Print.i(TAG, "TRIGGER AUTO LOGIN");
        triggerContentEvent(new LoginAutoHelper(), LoginAutoHelper.createAutoLoginBundle(), this);
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

                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                if (exist)
                {
                    ContentValues values = new ContentValues();
                    values.put("login[email]", mCustomerEmail);
                    values.put("login[password]", mCustomerPassword);
                    triggerContentEvent(new LoginHelper(), LoginHelper.createLoginBundle(values), this);

                }
                else
                {

                }
                // Validate next login step
                /*FragmentType fragmentType = exist ? FragmentType.LOGIN_EMAIL : FragmentType.REGISTER;
                // Go to next login step
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.DATA, mCustomerEmail);
                bundle.putBoolean(ConstantsIntentExtra.FLAG_1, isInCheckoutProcess);
                bundle.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
                getBaseActivity().onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);*/
                break;
            case AUTO_LOGIN_EVENT:
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
                // Get customer
                nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, false, false);
                // Finish
                getActivity().onBackPressed();
                return;
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
                showWarningErrorMessage(getString(R.string.error_invalid_email));
                // Show content
                showFragmentContentContainer();
                break;
            case AUTO_LOGIN_EVENT:
                // Logout
                LogOut.perform(getWeakBaseActivity());
            default:
                break;
        }
    }




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
            // Get error message
            mErrorMessage = (TextView) view.findViewById(R.id.login_text_error_message);
            // Get continue button
            view.findViewById(R.id.login_button_continue).setOnClickListener(NewSessionLoginMainFragment.this);
        }
    }

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
            mEmailView = (EditText) view.findViewById(R.id.login_email);
            mPasswordView = (EditText) view.findViewById(R.id.login_password);
            // Get error message
            mErrorMessage = (TextView) view.findViewById(R.id.login_text_error_message);
            // Get continue button
            // view.findViewById(R.id.login_button_continue).setOnClickListener(this);
        }
    }

}
