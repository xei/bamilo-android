package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.facebook.Request;
import com.facebook.Session;
import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.OnActivityFragmentInteraction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.maintenance.MaintenancePage;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author sergiopereira
 */
public abstract class BaseFragment extends Fragment implements OnActivityFragmentInteraction, OnClickListener, ViewStub.OnInflateListener {

    protected static final String TAG = BaseFragment.class.getSimpleName();

    public static final int RESTART_FRAGMENTS_DELAY = 500;

    public static final Boolean IS_NESTED_FRAGMENT = true;

    public static final Boolean IS_NOT_NESTED_FRAGMENT = false;

    public static final int NO_INFLATE_LAYOUT = 0;

    public static final int NO_TITLE = 0;

    private NavigationAction action;

    protected DialogFragment dialog;

    private Set<MyMenuItem> enabledMenuItems;

    private int mInflateLayoutResId = NO_INFLATE_LAYOUT;

    protected int titleResId;

    private int checkoutStep;

    protected Boolean isNestedFragment = false;

    private boolean isOrderSummaryPresent;

    protected int ORDER_SUMMARY_CONTAINER = R.id.order_summary_container;

    protected boolean isOnStoppingProcess = true;

    private BaseActivity mainActivity;

    private ViewStub mLoadingView;

    private View mRetryView;

    private View mContentView;

    private ViewStub mFallBackView;

    private ViewStub mMaintenanceView;

    protected long mLoadTime = 0l; // For tacking

    private Locale mLocale;

    public enum KeyboardState {NO_ADJUST_CONTENT, ADJUST_CONTENT}

    private KeyboardState adjustState = KeyboardState.ADJUST_CONTENT;

    protected TeaserGroupType mGroupType;

    private int mDeepLinkOrigin = DeepLinkManager.FROM_UNKNOWN;

    private ErrorLayoutFactory mErrorLayoutFactory;

    /**
     * Constructor with layout to inflate
     */
    public BaseFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int layoutResId, int titleResId, KeyboardState adjust_state) {
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = titleResId;
        this.adjustState = adjust_state;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor used only by nested fragments
     */
    public BaseFragment(Boolean isNestedFragment, int layoutResId) {
        this.isNestedFragment = isNestedFragment;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = 0;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor used only by PDV fragments
     */
    public BaseFragment(EnumSet<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId, KeyboardState adjust_state) {
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.titleResId = titleResId;
        this.adjustState = adjust_state;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor with layout to inflate used only by Checkout fragments
     */
    public BaseFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int layoutResId, int titleResId, KeyboardState adjust_state, int titleCheckout) {
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = titleResId;
        this.adjustState = adjust_state;
        this.checkoutStep = titleCheckout;
    }

    /**
     * #### LIFE CICLE ####
     */

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (BaseActivity) activity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            mGroupType =(TeaserGroupType) arguments.getSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE);
            mDeepLinkOrigin = arguments.getInt(ConstantsIntentExtra.DEEP_LINK_ORIGIN, DeepLinkManager.FROM_UNKNOWN);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get current time
        mLoadTime = System.currentTimeMillis();
        if (hasLayoutToInflate()) {
            Print.i(TAG, "ON CREATE VIEW: HAS LAYOUT TO INFLATE");
            View view = inflater.inflate(R.layout.fragment_root_layout, container, false);
            ViewStub contentContainer = (ViewStub) view.findViewById(R.id.content_container);
            contentContainer.setLayoutResource(mInflateLayoutResId);
            this.mContentView = contentContainer.inflate();
            return view;
        } else {
            Print.i(TAG, "ON CREATE VIEW: HAS NO LAYOUT TO INFLATE");
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.d(TAG, "ON VIEW CREATED");
        // Get current time
        if(mLoadTime == 0) mLoadTime = System.currentTimeMillis();
        // Set flag for requests
        isOnStoppingProcess = false;
        // Exist order summary
        isOrderSummaryPresent = view.findViewById(ORDER_SUMMARY_CONTAINER) != null;
        // Get content layout
        mContentView = view.findViewById(R.id.content_container);
        // Get loading layout
        mLoadingView = (ViewStub) view.findViewById(R.id.fragment_stub_loading);
        mLoadingView.setOnInflateListener(this);
        // Get retry layout
        mRetryView =  view.findViewById(R.id.fragment_stub_retry);
        ((ViewStub)mRetryView).setOnInflateListener(this);
        // Get fall back layout
        mFallBackView = (ViewStub) view.findViewById(R.id.fragment_stub_home_fall_back);
        mFallBackView.setOnInflateListener(this);
        // Get maintenance layout
        mMaintenanceView = (ViewStub) view.findViewById(R.id.fragment_stub_maintenance);
        mMaintenanceView.setOnInflateListener(this);
        // Hide search component for change country
        if (this.action == NavigationAction.Country) {
            // Hide search component
            getBaseActivity().hideActionBarItemsForChangeCountry(EnumSet.noneOf(MyMenuItem.class));
        }
        // Update base components, like items on action bar
        if (!isNestedFragment && enabledMenuItems != null) {
            Print.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems.toString() + " " + action.toString());
            getBaseActivity().updateBaseComponents(enabledMenuItems, action, titleResId, checkoutStep);
        }
    }

    /**
     * Show the summary order if the view is present
     *
     * @author sergiopereira
     */
    public void showOrderSummaryIfPresent(int checkoutStep, OrderSummary orderSummary) {
        // Get order summary
        if (isOrderSummaryPresent) {
            Print.i(TAG, "ORDER SUMMARY IS PRESENT");
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(ORDER_SUMMARY_CONTAINER, CheckoutSummaryFragment.getInstance(checkoutStep, orderSummary));
            ft.commit();
        } else {
            Print.i(TAG, "ORDER SUMMARY IS NOT PRESENT");
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.d(TAG, "ON RESUME");

        isOnStoppingProcess = false;

        if (getBaseActivity() != null && !isNestedFragment) {
            getBaseActivity().warningFactory.hideWarning();
        }

        /**
         * Adjust state for each fragment type.
         */
        if (!isNestedFragment || action == NavigationAction.Country) {
            updateAdjustState(this.adjustState, true);
        }

        if (null != getBaseActivity()) {
            getBaseActivity().hideSearchComponent();
            if(action != null){
                getBaseActivity().updateNavigationMenu(action);
            }
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
        /**
         * Restore locale if called the forceInputAlignToLeft(). 
         * Fix the input text align to right 
         */
        restoreInputLocale();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        // Set that fragment is on the stopping process
        isOnStoppingProcess = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isOnStoppingProcess = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Recycle bitmaps
        if (getView() != null) {
            unbindDrawables(getView());
        }
    }

    /**
     * #### MEMORY ####
     */

    /**
     * This method should be used when we known that the system clean data of application
     */
    public void restartAllFragments() {
        Print.w(TAG, "IMPORTANT DATA IS NULL - GOTO HOME -> " + mainActivity.toString());

        final BaseActivity activity = getBaseActivity();

        // wait 500ms before switching to HOME, to be sure all fragments ended any visual processing pending
        if (activity != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                }
            }, RESTART_FRAGMENTS_DELAY);
        } else {
            Print.w(TAG, "RESTART ALL FRAGMENTS - ERROR : Activity is NULL");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Print.i(TAG, "ON LOW MEMORY");
        // TODO - Validate this is necessary
        if (getView() != null && isHidden()) {
            unbindDrawables(getView());
        }
    }

    /**
     * Recycle bitmaps
     * @see <p>http://stackoverflow.com/questions/10314527/caused-by-java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget</p>
     *      <p>http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android</p>
     */
    public void unbindDrawables(View view) {
        Print.i(TAG, "UNBIND DRAWABLES");
        try {

            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            } else if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                if (view instanceof AdapterView<?>) {
                    return;
                }

                try {
                    ((ViewGroup) view).removeAllViews();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        } catch (RuntimeException e) {
            Print.w(TAG, "" + e);
        }
    }

    /**
     * #### BACK PRESSED ####
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.BaseActivity.OnActivityFragmentInteraction#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        if (mDeepLinkOrigin == DeepLinkManager.FROM_URI && getBaseActivity() != null) {
            getBaseActivity().finish();
            return true;
        }
        // No intercept the back pressed
        return false;
    }

    /**
     * #### TRIGGER EVENT ####
     */

    /**
     * Send request showing the loading
     */
    protected final void triggerContentEvent(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Show loading
        showFragmentLoading();
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * Send request and show progress view
     */
    protected final void triggerContentEventProgress(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Show progress
        showActivityProgress();
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * Send request
     */
    protected final void triggerContentEventNoLoading(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * Receive an update from other fragment
     */
    public void notifyFragment(Bundle bundle) {
        //...
    }

    /**
     * This method was created because the method on BaseActivity not working with dynamic forms
     */
    protected void hideKeyboard() {
        Print.d(TAG, "DYNAMIC FORMS: HIDE KEYBOARD");
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setActivity(BaseActivity activity) {
        mainActivity = activity;
    }

    /**
     * The variable mainActivity is setted onStart
     *
     * @return BaseActivity or null
     */
    public BaseActivity getBaseActivity() {
        if (mainActivity == null) {
            mainActivity = (BaseActivity) getActivity();
        }
        return mainActivity;
    }

    /**
     * Method used to redirect the native checkout to the old checkout method
     */
    public void gotoOldCheckoutMethod(BaseActivity activity, String email, String error) {
        Print.w(TAG, "WARNING: GOTO WEB CHECKOUT");
        // Tracking
        String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
        TrackerDelegator.trackNativeCheckoutError(userId, email, error);
        // Warning user
        Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
        // Remove native checkout
        getBaseActivity().removeAllNativeCheckoutFromBackStack();
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.CHECKOUT_BASKET);
        activity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Set screen response to keyboard request
     *
     * @param newAdjustState
     *            <code>KeyboardState</code> indicating if the layout will suffer any adjustment
     * @param force
     *            if <code>true</code> the adjustState is replaced by <code>newAdjustState</code>
     * @author Andre Lopes
     */
    private void updateAdjustState(KeyboardState newAdjustState, boolean force) {
        if (getBaseActivity() != null) {
            // Let that the definition of the softInputMode can be forced if the flag force is true
            if (force || BaseActivity.currentAdjustState != newAdjustState) {
                String stateString = "UNDEFINED";
                BaseActivity.currentAdjustState = newAdjustState;
                switch (newAdjustState) {
                    case NO_ADJUST_CONTENT:
                        stateString = "NO_ADJUST_CONTENT";
                        getBaseActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        break;
                    case ADJUST_CONTENT:
                        stateString = "ADJUST_CONTENT";
                        getBaseActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        break;
                }
                Print.d(TAG, "UPDATE ADJUST STATE: " + stateString);
            }
        }
    }

    /*
     * ########### ROOT VIEWS ###########
     */

    /**
     * Validate if is to inflate a fragment layout into root layout
     * @return true/false
     * @author sergiopereira
     */
    private boolean hasLayoutToInflate() {
        return mInflateLayoutResId > NO_INFLATE_LAYOUT;
    }

    /**
     * Show the content fragment from the root layout
     */
    protected void showFragmentContentContainer() {
        UIUtils.showOrHideViews(View.VISIBLE, mContentView);
        UIUtils.showOrHideViews(View.GONE, mLoadingView, mRetryView, mFallBackView, mMaintenanceView);
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentNoNetworkRetry() {
        showErrorFragment(ErrorLayoutFactory.NO_NETWORK_LAYOUT, this);
    }

    /**
     * Show the loading view from the root layout
     */
    protected void showFragmentLoading() {
        UIUtils.showOrHideViews(View.VISIBLE, mLoadingView);
    }

    /**
     * Show continue with listener for going back.
     * @author sergiopereira
     */
    protected void showContinueShopping() {
        Print.i(TAG, "ON SHOW CONTINUE LAYOUT");
        showErrorFragment(ErrorLayoutFactory.CONTINUE_SHOPPING_LAYOUT, this);
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentErrorRetry() {
        showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
    }

    protected final void showErrorFragment(int type, OnClickListener listener){
        if(mRetryView instanceof ViewStub){
            mRetryView.setTag(mRetryView.getId(), type);
            mRetryView.setTag(R.id.stub_listener, listener);
            ((ViewStub)mRetryView).inflate();
        } else {
            mErrorLayoutFactory.showErrorLayout(type);
            View retryButton = mRetryView.findViewById(R.id.fragment_root_error_button);
            retryButton.setOnClickListener(listener);
            retryButton.setTag(R.id.fragment_root_error_button, type);
            UIUtils.showOrHideViews(View.VISIBLE, mRetryView);
        }
    }

    /**
     * Show the fall back view from the root layout
     */
    protected void showFragmentFallBack() {
        UIUtils.showOrHideViews(View.VISIBLE, mFallBackView);
    }

    /**
     * Show the maintenance page
     */
    public void showFragmentMaintenance() {
        UIUtils.showOrHideViews(View.VISIBLE, mMaintenanceView);
    }

    /**
     * Show BaseActivity progress loading
     */
    protected void showActivityProgress() {
        getBaseActivity().showProgress();
    }

    /**
     * Hide BaseActivity progress loading
     */
    protected void hideActivityProgress() {
        getBaseActivity().dismissProgress();
    }


    protected void showNoNetworkWarning() {
        getBaseActivity().warningFactory.showWarning(WarningFactory.NO_INTERNET);
        hideActivityProgress();
        showFragmentContentContainer();
    }

    protected void showUnexpectedErrorWarning() {
        getBaseActivity().warningFactory.showWarning(WarningFactory.PROBLEM_FETCHING_DATA_ANIMATION);
        showFragmentContentContainer();
        hideActivityProgress();
    }

    /**
     * Set the inflated stub
     * @param stub The view stub
     * @param inflated The inflated view
     */
    @Override
    public void onInflate(ViewStub stub, View inflated) {
        // Get stub id
        int id = stub.getId();
        // Case home fall back
        if(id == R.id.fragment_stub_home_fall_back)  {
            onInflateHomeFallBack(inflated);
        }
        // Case loading
        else if(id == R.id.fragment_stub_loading) {
            onInflateLoading(inflated);
        }
        // Case no network
        else if(id == R.id.fragment_stub_retry) {
            onInflateNoNetwork(stub, inflated);
        }
        // Case maintenance
        else if(id == R.id.fragment_stub_maintenance) {
            onInflateMaintenance(inflated);
        }
        // Case unknown
        else {
            Print.w(TAG, "WARNING: UNKNOWN INFLATED STUB");
        }
    }

    /**
     * Set the home fall back view.
     */
    private void onInflateHomeFallBack(View inflated) {
        Print.i(TAG, "ON INFLATE STUB: FALL BACK");
        try {
            boolean isSingleShop = getResources().getBoolean(R.bool.is_single_shop_country);
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, getString(R.string.app_name));
            TextView fallbackBest = (TextView) inflated.findViewById(R.id.fallback_best);
            TextView fallbackCountry = (TextView) inflated.findViewById(R.id.fallback_country);
            View countryD = inflated.findViewById(R.id.fallback_country_double);
            TextView bottomCountry = (TextView) inflated.findViewById(R.id.fallback_country_bottom);
            TextView topCountry = (TextView) inflated.findViewById(R.id.fallback_country_top);
            fallbackBest.setText(R.string.fallback_best);
            if (country.split(" ").length == 1) {
                fallbackCountry.setText(country.toUpperCase());
                fallbackCountry.setVisibility(View.VISIBLE);
                countryD.setVisibility(View.GONE);
                fallbackCountry.setText(isSingleShop ? "" : country.toUpperCase());
                if(getResources().getBoolean(R.bool.is_bamilo_specific)){
                    getView().findViewById(R.id.home_fallback_country_map).setVisibility(View.GONE);
                }
            } else {
                topCountry.setText(country.split(" ")[0].toUpperCase());
                bottomCountry.setText(country.split(" ")[1].toUpperCase());
                fallbackBest.setTextSize(11.88f);
                countryD.setVisibility(View.VISIBLE);
                fallbackCountry.setVisibility(View.GONE);
            }
            fallbackBest.setSelected(true);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mRetryView, mMaintenanceView, mLoadingView);
    }

    /**
     * Set the loading view.
     */
    protected void onInflateLoading(View inflated) {
        Print.i(TAG, "ON INFLATE STUB: LOADING");
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mRetryView, mFallBackView, mMaintenanceView);
    }

    /**
     * Set no network view.
     * @param inflated The inflated view
     */
    protected void onInflateNoNetwork(ViewStub viewStub, View inflated) {
        Print.i(TAG, "ON INFLATE STUB: RETRY");

        mRetryView = inflated;

        mErrorLayoutFactory = new ErrorLayoutFactory((ViewGroup)inflated);
        showErrorFragment((int) viewStub.getTag(viewStub.getId()), (OnClickListener) viewStub.getTag(R.id.stub_listener));

    }

    /**
     * Set maintenance view.
     * @param inflated The inflated view
     */
    private void onInflateMaintenance(View inflated) {
        Print.i(TAG, "ON INFLATE STUB: UNEXPECTED ERROR");
        // Validate venture
        if (getResources().getBoolean(R.bool.is_bamilo_specific)) {
            MaintenancePage.setMaintenancePageBamilo(inflated, this);
        } else {
            MaintenancePage.setMaintenancePageBaseActivity(getBaseActivity(), this);
        }
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mRetryView, mFallBackView, mLoadingView);
    }

    /*
     * ########### INPUT FORMS ###########
     */

    /**
     * Force input align to left
     *
     * @author sergiopereira
     * @see {@link CheckoutAboutYouFragment#onResume()} <br> {@link SessionLoginFragment#onResume()}
     */
    protected void forceInputAlignToLeft() {
        if (getBaseActivity() != null && !getBaseActivity().getApplicationContext().getResources().getBoolean(R.bool.is_bamilo_specific)) {
            // Save the default locale
            mLocale = Locale.getDefault();
            // Force align to left
            Locale.setDefault(Locale.US);
        }
    }

    /**
     * Restore the saved locale {@link #onResume()} if not null.
     *
     * @author sergiopereira
     */
    protected void restoreInputLocale() {
        if (mLocale != null) {
            Locale.setDefault(mLocale);
        }
    }

    /**
     * Get load time used for tracking
     *
     * @author paulo
     */
    protected long getLoadTime() {
        return mLoadTime;
    }

    /*
     * ########### RESPONSE ###########
     */

    /**
     * Handle success response
     * @param bundle The success bundle
     * @return intercept or not
     */
    public boolean handleSuccessEvent(Bundle bundle) {
        Print.i(TAG, "ON HANDLE SUCCESS EVENT");
        // Validate event
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
            case GET_SHOPPING_CART_ITEMS_EVENT:
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                getBaseActivity().updateCartInfo();
                return true;
            case LOGOUT_EVENT:
                Print.i(TAG, "LOGOUT EVENT");
                getBaseActivity().onLogOut();
                return true;
            case LOGIN_EVENT:
                JumiaApplication.INSTANCE.setLoggedIn(true);
                getBaseActivity().triggerGetShoppingCartItemsHelper();
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Handle error response.
     * @param bundle The error bundle
     * @return intercept or not
     */
    @SuppressWarnings("unchecked")
    public boolean handleErrorEvent(final Bundle bundle) {
        Print.i(TAG, "ON HANDLE ERROR EVENT");

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        EventTask eventTask = (EventTask) bundle.getSerializable(Constants.BUNDLE_EVENT_TASK);

        if (!bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY)) {
            return false;
        }

        if (errorCode == null) {
            return false;
        }
//        errorCode = ErrorCode.IO;
        Print.i(TAG, "ON HANDLE ERROR EVENT: " + errorCode.toString());
        if (errorCode.isNetworkError()) {
            switch (errorCode) {
                case IO:
                case CONNECT_ERROR:
                    if(eventTask == EventTask.SMALL_TASK) {
                        showUnexpectedErrorWarning();
                    } else {
                        showFragmentErrorRetry();
                    }
                    return true;
                case TIME_OUT:
                case NO_NETWORK:
                    // Show no network layout
                    if(eventTask == EventTask.SMALL_TASK){
                        showNoNetworkWarning();
                    } else {
                        showFragmentNoNetworkRetry();
                    }
                    return true;
                case HTTP_STATUS:
                    // Case HOME show retry otherwise show continue
                    if(action == NavigationAction.Home) {
                        showFragmentErrorRetry();
                    } else {
                        showContinueShopping();
                    }
                    return true;
                case SSL:
                case SERVER_IN_MAINTENANCE:
                    showFragmentMaintenance();
                    return true;
                case REQUEST_ERROR:
                    HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                    String dialogMsg = "";
                    if (validateMessages == null || validateMessages.isEmpty()) {
                        validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                    }
                    if (validateMessages != null) {
                        for (String message : validateMessages) {
                            dialogMsg += message + "\n";
                        }
                    } else {
                        for (Entry<String, ? extends List<String>> entry : errorMessages.entrySet()) {
                            dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                        }
                    }
                    if (dialogMsg.equals("")) {
                        dialogMsg = getString(R.string.validation_errortext);
                    }
                    // showContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.validation_title), dialogMsg,
                            getResources().getString(R.string.ok_label), "", new OnClickListener() {

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
                case SERVER_OVERLOAD:
                    if(getBaseActivity() != null){
                        ActivitiesWorkFlow.showOverLoadErrorActivity(getBaseActivity());
                        showFragmentErrorRetry();
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;

    }

    protected void clearCredentials() {
        JumiaApplication.INSTANCE.setLoggedIn(false);
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
    }

    /*
     * ########### LISTENERS ###########
     */

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.fragment_root_error_button){
            checkErrorButtonBehavior(view);
        }
        // Case retry button in maintenance page
        else if(id == R.id.fragment_root_retry_maintenance) {
            onClickRetryMaintenance(view);
        }
        // Case choose country button in maintenance page
        else if(id == R.id.fragment_root_cc_maintenance) {
            onClickMaintenanceChooseCountry();
        }
        // Case unknown
        else {
            Print.w(TAG, "WARNING: UNKNOWN CLICK EVENT");
        }
    }

    private void checkErrorButtonBehavior(View view) {
        if(view.getId() == R.id.fragment_root_error_button){
            int error = (int)view.getTag(R.id.fragment_root_error_button);

            if(error == ErrorLayoutFactory.NO_NETWORK_LAYOUT){
                // Case retry button from network
                onClickRetryNoNetwork(view);
            } else if(error == ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT){
                // Case retry button from error
                onClickRetryUnexpectedError(view);
            } else {
                // Case continue button
                onClickContinueButton();
            }
        }
    }

    /**
     * Process the click on retry button in no network.
     * @param view The clicked view
     */
    protected void onClickRetryNoNetwork(View view) {
        try {
            Animation animation = AnimationUtils.loadAnimation(getBaseActivity(), R.anim.anim_rotate);
            View retrySpinning = view.findViewById(R.id.fragment_root_error_spinning);
            retrySpinning.clearAnimation();
            retrySpinning.setAnimation(animation);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
        }
        // Common method for retry buttons
        onClickRetryButton(view);
    }

    /**
     * Process the click on retry button in unexpected error.
     * @param view The clicked view
     */
    protected void onClickRetryUnexpectedError(View view) {
        try {
            Animation animation = AnimationUtils.loadAnimation(getBaseActivity(), R.anim.anim_rotate);
            View retrySpinning = view.findViewById(R.id.fragment_root_error_spinning);
            retrySpinning.clearAnimation();
            retrySpinning.setAnimation(animation);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
        }
        // Common method for retry buttons
        onClickRetryButton(view);
    }

    /**
     * Process the click on retry button in maintenance page.
     * @param view The clicked view
     */
    protected void onClickRetryMaintenance(View view) {
        // Common method for retry buttons
        onClickRetryButton(view);
    }

    /**
     * Process the click in continue shopping
     * @param view The clicked view
     * @author sergiopereira
     */
    protected void onClickRetryButton(View view) {
        // ...
    }

    /**
     * Process the click in continue shopping
     *
     * @author sergiopereira
     */
    protected void onClickContinueButton() {
        getBaseActivity().onBackPressed();
    }

    /**
     * Process the click on choose country button in maintenance page.
     *
     * @author sergiopereira
     */
    private void onClickMaintenanceChooseCountry() {
        // Show Change country
        getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
        getBaseActivity().onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /*
     * ########### FACEBOOK ###########
     */

    /**
     * Clean the current session and warning user.
     */
    protected final void onUserNotAcceptRequiredPermissions() {
        Print.i(TAG, "USER NOT ACCEPT THE SECOND FACEBOOK DIALOG");
        // Clean session
        clearCredentials();
        FacebookHelper.cleanFacebookSession();
        // Notify user
        ToastFactory.ERROR_FB_PERMISSION.show(getBaseActivity());
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Perform a new request to user with required permissions
     * @param session The Facebook session
     * @param callback The requester
     */
    protected final void onMakeNewRequiredPermissionsRequest(Session session, Session.StatusCallback callback) {
        Print.i(TAG, "USER NOT ACCEPT EMAIL PERMISSION");
        // Show loading
        showFragmentLoading();
        // Make new permissions request
        FacebookHelper.makeNewRequiredPermissionsRequest(this, session, callback);
    }

    /**
     * Get the FacebookGraphUser.
     * @param session The Facebook session
     * @param callback The requester
     */
    protected final void onMakeGraphUserRequest(Session session, Request.GraphUserCallback callback) {
        Print.i(TAG, "USER ACCEPT PERMISSIONS");
        // Show loading
        showFragmentLoading();
        // Make request to the me API
        FacebookHelper.makeGraphUserRequest(session, callback);
    }

    /*
     * ########### ALERT DIALOGS ###########
     */

    /**
     * Dismiss the current dialog
     */
    protected void dismissDialogFragment() {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    /**
     * Process the product click
     * @author sergiopereira
     */
    protected void onClickProduct(String targetUrl, Bundle bundle) {
        Print.i(TAG, "ON CLICK PRODUCT");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.i(TAG, "WARNING: URL IS NULL");
        }
    }

    /**
     * Process the click on shops in shop
     *
     * @param url    The url for CMS block
     * @param title  The shop title
     * @param bundle The new bundle
     */
    protected void onClickInnerShop(String url, String title, Bundle bundle) {
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        getBaseActivity().onSwitchFragment(FragmentType.INNER_SHOP, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the catalog click
     */
    protected void onClickCatalog(String targetUrl, String targetTitle, Bundle bundle) {
        Print.i(TAG, "ON CLICK CATALOG");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        } else {
            Print.w(TAG, "WARNING: URL IS NULL");
        }
    }

    /**
     * Create an array with a single campaign
     */
    protected ArrayList<TeaserCampaign> createSingleCampaign(String targetTitle, String targetUrl) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(targetTitle);
        campaign.setUrl(targetUrl);
        campaigns.add(campaign);
        return campaigns;
    }

}