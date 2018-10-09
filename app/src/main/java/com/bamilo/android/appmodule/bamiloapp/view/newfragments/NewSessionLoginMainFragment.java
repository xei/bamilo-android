package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.bamilo.android.appmodule.bamiloapp.adapters.SimplePagerAdapter;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginAutoHelper;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.LogOut;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.tracking.emarsys.EmarsysTracker;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 */
public class NewSessionLoginMainFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewSessionLoginMainFragment.class.getSimpleName();




    private ViewPager viewPager;
    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    /*TabLayout.Tab tabLogin;
    TabLayout.Tab tabRegister;*/
    //DROID-10
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private LinearLayout loginRoot;
    private TabLayout tabLayout;
    private View rootView;
    private boolean viewInitiated = false;
    private boolean autoLoginFailed = false;
    private int currentTabPosition = 1;

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        viewInitiated = false;
        this.rootView = view;
        if (autoLoginFailed || (mNextStepFromParent == null && !isInCheckoutProcess)) {
            initViews();
        }

//        tabLogin = tabLayout.newTab();
//        tabLogin.setTag("Login");
//        tabLayout.addTab(tabLogin);
//        tabLogin.setCustomView(R.layout.tab_login);


//        tabRegister = tabLayout.newTab();
//        tabLayout.addTab(tabRegister);
//        tabRegister.setTag("Register");
//        tabRegister.setCustomView(R.layout.tab_register);
        //tabLayout.setupWithViewPager(viewPager);
        /*tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });*/
        /*tabRegister.select();
        tabLogin.select();*/

        /*if (savedInstanceState != null)
        {
            int selectedTab = savedInstanceState.getInt("selectedtab", 0);
            if (selectedTab == 1) {
                tabRegister.select();
            }
        }*/


    }

    private void initViews() {
        loginRoot = (LinearLayout) rootView.findViewById(R.id.login_root);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager();
        viewInitiated = true;
    }

    private void setupViewPager() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(registerFragment);
        fragments.add(loginFragment);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.register_label));
        titles.add(getString(R.string.login_label));

        SimplePagerAdapter adapter = new SimplePagerAdapter(getChildFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentTabPosition);
        getBaseActivity().setUpExtraTabLayout(viewPager);
        tabLayout = getBaseActivity().getExtraTabLayout();
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.orange_lighter));
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.black_700),
                Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(tabLayout);
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

        // Case auto login
        if (BamiloApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            triggerAutoLogin();
        }
        // Case show content
        else {
            if (!viewInitiated) {
                initViews();
            }
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
        if (tabLayout != null) {
            outState.putInt("selectedtab", tabLayout.getSelectedTabPosition());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        if (viewPager != null) {
            currentTabPosition = viewPager.getCurrentItem();
        }

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
     * ################ LISTENERS ################
     */



     /*
     * ################ LISTENERS ################
     */

    /*@Override
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
    }*/








    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    /*
     * ################ TRIGGERS ################
     */



    private void triggerAutoLogin() {
        triggerContentEvent(new LoginAutoHelper(getContext()), LoginAutoHelper.createAutoLoginBundle(), this);
    }



    /*
     * ################ RESPONSE ################
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            /*case EMAIL_CHECK:
                //DROID-10
                TrackerDelegator.trackScreenLoadTiming(R.string.gaLogin, mGABeginRequestMillis, mCustomerEmail);
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
                    hideActivityProgress();

                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));

                }

                break;*/
            case AUTO_LOGIN_EVENT://Emarsys
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();
                // Case valid next step
                if(nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                    // Tracking
                    TrackerDelegator.trackLoginSuccessful(customer, true, false);

                    // Global Tracker
                    MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_SUCCESS,
                            Constants.LOGIN_METHOD_EMAIL, customer.getId(),
                            MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, EmailHelper.getHost(customer.getEmail()),
                                    true));
                    TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);

                    EmarsysTracker.getInstance().trackEventAppLogin(Integer.parseInt(getContext().getResources().getString(R.string.Emarsys_ContactFieldID)),BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);

                    // Validate the next step
                    CheckoutStepManager.validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi, getArguments());
                    /*RecommendManager recommendManager = new RecommendManager();
                    recommendManager.setEmail(BamiloApplication.CUSTOMER.getEmail(), ""+BamiloApplication.CUSTOMER.getId());*/
                }
                // Case unknown checkout step
                else {
                    hideActivityProgress();
                    // Show layout to call to order
                    showFragmentUnknownCheckoutStepError();
                }
                getBaseActivity().setupDrawerNavigation();
                break;
            /*case LOGIN_EVENT:
                hideActivityProgress();
                // Get customer
                nextStepStruct = (NextStepStruct) baseResponse.getContentData();

                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, false, false);
                // Finish

                if (isInCheckoutProcess)
                {
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, null, FragmentController.ADD_TO_BACK_STACK);
                    getActivity().onBackPressed();
                }
                else
                {
                    getActivity().onBackPressed();
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
            return;
        }
        // Validate messageItem o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            /*case EMAIL_CHECK:
                // Show warning
                mLoginErrorMessage.setText(getResources().getString(R.string.error_invalid_email));
                mLoginErrorMessage.setVisibility(View.VISIBLE);
                //showWarningErrorMessage(getString(R.string.error_invalid_email));
                // Show content
                showFragmentContentContainer();
                break;*/
            case AUTO_LOGIN_EVENT:
                // Logout
                LogOut.perform(getWeakBaseActivity(), null);
                autoLoginFailed = true;
                if (!viewInitiated) {
                    initViews();
                }
           /* case REGISTER_ACCOUNT_EVENT:
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
                break;*/
            default:
                break;
        }
    }

}
