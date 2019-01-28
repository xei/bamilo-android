package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.adapters.SimplePagerAdapter;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.LogOut;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginAutoHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Class used to perform login via Facebook,
 *
 * @author sergiopereira
 */
public class NewSessionLoginMainFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewSessionLoginMainFragment.class.getSimpleName();

    private ViewPager viewPager;
    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.new_session_main_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mParentFragmentType = (FragmentType) arguments
                    .getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            mNextStepFromParent = (FragmentType) arguments
                    .getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            isInCheckoutProcess = arguments
                    .getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);
        }
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewInitiated = false;
        this.rootView = view;
        if (autoLoginFailed || (mNextStepFromParent == null && !isInCheckoutProcess)) {
            initViews();
        }
    }

    private void initViews() {
        loginRoot = rootView.findViewById(R.id.login_root);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        viewPager = rootView.findViewById(R.id.viewpager);
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

        SimplePagerAdapter adapter = new SimplePagerAdapter(getChildFragmentManager(), fragments,
                titles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentTabPosition);
        getBaseActivity().setUpExtraTabLayout(viewPager);
        tabLayout = getBaseActivity().getExtraTabLayout();
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(
                ContextCompat.getColor(getContext(), R.color.orange_lighter));
        tabLayout.setTabTextColors(ContextCompat.getColor(getContext(), R.color.black_700),
                Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
        outState.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, mNextStepFromParent);
        outState.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, isInCheckoutProcess);
        if (tabLayout != null) {
            outState.putInt("selectedtab", tabLayout.getSelectedTabPosition());
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (viewPager != null) {
            currentTabPosition = viewPager.getCurrentItem();
        }

        getBaseActivity().hideKeyboard();
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    private void triggerAutoLogin() {
        triggerContentEvent(new LoginAutoHelper(getContext()),
                LoginAutoHelper.createAutoLoginBundle(), this);
    }

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
            case AUTO_LOGIN_EVENT://Emarsys
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();
                // Case valid next step
                if (nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject())
                            .getCustomer();
                    // Tracking
                    TrackerDelegator.trackLoginSuccessful(customer, true, false);

                    // Global Tracker
                    MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT,
                            EventActionKeys.LOGIN_SUCCESS,
                            Constants.LOGIN_METHOD_EMAIL, customer.getId(),
                            MainEventModel
                                    .createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL,
                                            EmailHelper.getHost(customer.getEmail()),
                                            true));
//                    TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);

                    EventTracker.INSTANCE.login(
                            String.valueOf(customer.getId()),
                            String.valueOf(customer.getEmail()),
                            String.valueOf(customer.getPhoneNumber()),
                            TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL);

                    // Validate the next step
                    CheckoutStepManager
                            .validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess,
                                    mParentFragmentType, mNextStepFromParent, nextStepFromApi,
                                    getArguments());
                } else {
                    hideActivityProgress();
                    // Show layout to call to order
                    showFragmentUnknownCheckoutStepError();
                }
                getBaseActivity().setupDrawerNavigation();
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
            return;
        }
        // Validate messageItem o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {

            case AUTO_LOGIN_EVENT:
                // Logout
                LogOut.perform(getWeakBaseActivity(), null);
                autoLoginFailed = true;
                if (!viewInitiated) {
                    initViews();
                }
            default:
                break;
        }
    }
}
