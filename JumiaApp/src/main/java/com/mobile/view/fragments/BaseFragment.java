package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
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

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.home.type.TeaserGroupType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.OnActivityFragmentInteraction;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.maintenance.MaintenancePage;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.TabLayoutUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
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

    @NavigationAction.Type
    private int action;

    protected DialogFragment dialog;

    private Set<MyMenuItem> enabledMenuItems;

    private int mInflateLayoutResId = NO_INFLATE_LAYOUT;

    protected int titleResId;

    @ConstantsCheckout.CheckoutType
    protected int checkoutStep;

    protected Boolean isNestedFragment = false;

    private boolean isOrderSummaryPresent;

    protected int ORDER_SUMMARY_CONTAINER = R.id.order_summary_container;

    protected boolean isOnStoppingProcess = true;

    private BaseActivity mainActivity;

    private ViewStub mLoadingView;

    private View mErrorView;

    private View mContentView;

    private ViewStub mFallBackView;

    private ViewStub mMaintenanceView;

    protected long mLoadTime = 0; // For tacking

    public static final int NO_ADJUST_CONTENT = 0;
    public static final int ADJUST_CONTENT = 1;
    @IntDef({NO_ADJUST_CONTENT, ADJUST_CONTENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyboardState{}

    @KeyboardState
    private int adjustState = ADJUST_CONTENT;

    protected TeaserGroupType mGroupType;

    private int mDeepLinkOrigin = DeepLinkManager.FROM_UNKNOWN;

    private ErrorLayoutFactory mErrorLayoutFactory;

    /**
     * Constructor with layout to inflate
     */
    public BaseFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = titleResId;
        this.adjustState = adjustState;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor used only by nested fragments
     */
    public BaseFragment(Boolean isNestedFragment, @LayoutRes int layoutResId) {
        this.isNestedFragment = isNestedFragment;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = 0;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor with layout to inflate used only by Checkout fragments
     */
    public BaseFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int  adjustState, @ConstantsCheckout.CheckoutType int titleCheckout) {
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = titleResId;
        this.adjustState = adjustState;
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
            mGroupType =(TeaserGroupType) arguments.getSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE);
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
        // Get loading layout
        mLoadingView = (ViewStub) view.findViewById(R.id.fragment_stub_loading);
        mLoadingView.setOnInflateListener(this);
        // Get retry layout
        mErrorView =  view.findViewById(R.id.fragment_stub_retry);
        ((ViewStub) mErrorView).setOnInflateListener(this);
        // Get fall back layout
        mFallBackView = (ViewStub) view.findViewById(R.id.fragment_stub_home_fall_back);
        mFallBackView.setOnInflateListener(this);
        // Get maintenance layout
        mMaintenanceView = (ViewStub) view.findViewById(R.id.fragment_stub_maintenance);
        mMaintenanceView.setOnInflateListener(this);
        // Update base components, like items on action bar
        if (!isNestedFragment && enabledMenuItems != null) {
            Print.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems + " " + action);
            getBaseActivity().updateBaseComponents(enabledMenuItems, action, titleResId, checkoutStep);
            // Method used to set a bottom margin
            TabLayoutUtils.setViewWithoutNestedScrollView(mContentView, action);
        }
    }

    /**
     * Show the summary order if the view is present
     *
     * @author sergiopereira
     */
    public void showOrderSummaryIfPresent(int checkoutStep, PurchaseEntity orderSummary) {
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

        /**
         * Adjust state for each fragment type.
         */
        if (!isNestedFragment || action == NavigationAction.COUNTRY) {
            updateAdjustState(this.adjustState, true);
        }

        if (null != getBaseActivity()) {
            getBaseActivity().hideSearchComponent();
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
    }

    /**
     * #### MEMORY ####
     */

    /**
     * This method should be used when we known that the system clean data of application
     */
    public void restartAllFragments() {
        Print.w(TAG, "IMPORTANT DATA IS NULL - GOTO HOME -> " + mainActivity);
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
//        // TODO - Validate this is necessary
//        if (getView() != null && isHidden()) {
//            unbindDrawables(getView());
//        }
    }

//    /**
//     * Recycle bitmaps
//     * @see <p>http://stackoverflow.com/questions/10314527/caused-by-java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget</p>
//     *      <p>http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android</p>
//     */
//    public void unbindDrawables(View view) {
//        Print.i(TAG, "UNBIND DRAWABLES");
//        try {
//
//            if (view.getBackground() != null) {
//                view.getBackground().setCallback(null);
//            } else if (view instanceof ViewGroup) {
//                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                    unbindDrawables(((ViewGroup) view).getChildAt(i));
//                }
//                if (view instanceof AdapterView<?>) {
//                    return;
//                }
//
//                try {
//                    ((ViewGroup) view).removeAllViews();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (RuntimeException e) {
//            Print.w(TAG, "" + e);
//        }
//    }

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
     * Create a BaseActivity weak reference.
     */
    public WeakReference<BaseActivity> getWeakBaseActivity() {
        return new WeakReference<>(getBaseActivity());
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
    private void updateAdjustState(@KeyboardState int newAdjustState, boolean force) {
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
        UIUtils.showOrHideViews(View.GONE, mLoadingView, mErrorView, mFallBackView, mMaintenanceView);
    }

    /**
     * This method is used to set the fragment content invisible to recalculate the view and trigger the view listener<br>
     * Used by CreateAddressFragment
     */
    protected void showGhostFragmentContentContainer() {
        UIUtils.showOrHideViews(View.INVISIBLE, mContentView);
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
    protected void showFragmentSSLError() {
        showErrorFragment(ErrorLayoutFactory.SSL_ERROR_LAYOUT, this);
    }

    /**
     * Show the layout to call to order info
     * @author sergiopereira
     */
    protected void showFragmentUnknownCheckoutStepError() {
        showErrorFragment(ErrorLayoutFactory.UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT, this);
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentErrorRetry() {
        showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
    }

    /**
     * Show error layout based on type. if the view is not inflated, it will be in first place.
     */
    protected final void showErrorFragment(@ErrorLayoutFactory.LayoutErrorType int type, OnClickListener listener){
        if(mErrorView instanceof ViewStub){
            // If not inflated yet
            mErrorView.setTag(mErrorView.getId(), type);
            mErrorView.setTag(R.id.stub_listener, listener);
            ((ViewStub) mErrorView).inflate();
        } else {
            //If already inflated
            View retryButton = mErrorView.findViewById(R.id.fragment_root_error_button);
            retryButton.setOnClickListener(listener);
            retryButton.setTag(R.id.fragment_root_error_button, type);
            mErrorLayoutFactory.showErrorLayout(type);
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
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.no_internet_access_warning_title));
        hideActivityProgress();
        showFragmentContentContainer();
    }

    protected void showUnexpectedErrorWarning() {
        getBaseActivity().showWarning(WarningFactory.PROBLEM_FETCHING_DATA_ANIMATION);
        showFragmentContentContainer();
        hideActivityProgress();
    }

    public void showInfoAddToShoppingCartCompleted() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.added_to_shop_cart_dialog_text));
        }
    }

    public void showInfoAddToShoppingCartFailed() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_add_to_shopping_cart));
        }
    }

    public void showInfoAddBundleToShoppingCartCompleted() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.added_bundle_to_shop_cart_dialog_text));
        }
    }

    public void showInfoAddBundleToShoppingCartFailed(String errorMessage) {
        if(getBaseActivity() != null) {
            if(TextUtils.isEmpty(errorMessage))
                errorMessage = getString(R.string.error_add_bundle_to_shopping_cart);
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, errorMessage);
        }
    }

    public void showInfoLoginSuccess() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
        }
    }

    public void showInfoAddToShoppingCartOOS() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_outof_stock));
        }
    }

    public void showInfoAddToSaved() {
        if(getBaseActivity() != null) {
            getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.products_removed_saved));
        }
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
            onInflateErrorLayout(stub, inflated);
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
                if(ShopSelector.isRtlShop()){
                    inflated.findViewById(R.id.home_fallback_country_map).setVisibility(View.GONE);
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
        UIUtils.showOrHideViews(View.GONE, mContentView, mErrorView, mMaintenanceView, mLoadingView);
    }

    /**
     * Set the loading view.
     */
    protected void onInflateLoading(View inflated) {
        Print.i(TAG, "ON INFLATE STUB: LOADING");
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mErrorView, mFallBackView, mMaintenanceView);
    }

    /**
     * Set no network view.
     * @param inflated The inflated view
     */
    protected void onInflateErrorLayout(ViewStub viewStub, View inflated) {
        Print.i(TAG, "ON INFLATE STUB: ERROR LAYOUT");
        mErrorView = inflated;
        // Init error factory
        mErrorLayoutFactory = new ErrorLayoutFactory((ViewGroup)inflated);
        @ErrorLayoutFactory.LayoutErrorType int type = (int) viewStub.getTag(viewStub.getId());
        showErrorFragment(type, (OnClickListener) viewStub.getTag(R.id.stub_listener));
    }

    /**
     * Set maintenance view.
     * @param inflated The inflated view
     */
    private void onInflateMaintenance(View inflated) {
        Print.i(TAG, "ON INFLATE STUB: UNEXPECTED ERROR");
        // Validate venture
        if (ShopSelector.isRtlShop()) {
            MaintenancePage.setMaintenancePageBamilo(inflated, this);
        } else {
            MaintenancePage.setMaintenancePageBaseActivity(getBaseActivity(), this);
        }
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mErrorView, mFallBackView, mLoadingView);
    }

    /*
     * ########### INPUT FORMS ###########
     */

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
     * @param baseResponse The success response
     * @return intercept or not
     */
    public boolean handleSuccessEvent(BaseResponse baseResponse) {
        Print.i(TAG, "ON HANDLE SUCCESS EVENT");
        // Validate event
        EventType eventType = baseResponse.getEventType();
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
            case GUEST_LOGIN_EVENT:
            case FACEBOOK_LOGIN_EVENT:
            case LOGIN_EVENT:
                // TODO ADD HERE COMMON METHODS
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Handle error response.
     * @param response The error bundle
     * @return intercept or not
     */
    @SuppressWarnings("unchecked")
    public boolean handleErrorEvent(final BaseResponse response) {
        Print.i(TAG, "ON HANDLE ERROR EVENT");

        int errorCode = response.getError().getCode();
        EventTask eventTask = response.getEventTask();

        if (!response.isPriority()) {
            return false;
        }

        Print.i(TAG, "ON HANDLE ERROR EVENT: " + errorCode);
        if (ErrorCode.isNetworkError(errorCode)) {
            switch (errorCode) {
                case ErrorCode.IO:
                case ErrorCode.CONNECT_ERROR:
                    if(eventTask == EventTask.ACTION_TASK) {
                        showUnexpectedErrorWarning();
                    } else {
                        showFragmentErrorRetry();
                    }
                    return true;
                case ErrorCode.TIME_OUT:
                case ErrorCode.NO_CONNECTIVITY:
                    // Show no network layout
                    if(eventTask == EventTask.ACTION_TASK){
                        showNoNetworkWarning();
                    } else {
                        showFragmentNoNetworkRetry();
                    }
                    return true;
                case ErrorCode.HTTP_STATUS:
                    // Case HOME show retry otherwise show continue
                    if(action == NavigationAction.HOME) {
                        showFragmentErrorRetry();
                    } else {
                        showContinueShopping();
                    }
                    return true;
                case ErrorCode.SSL:
                case ErrorCode.SERVER_IN_MAINTENANCE:
                    showFragmentMaintenance();
                    return true;
                case ErrorCode.REQUEST_ERROR:
                    // Get error message or validate message
                    String msg = TextUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getValidateMessage();
                    // Get generic message
                    if(TextUtils.isEmpty(msg)) {
                        msg = getString(R.string.error_please_try_again);
                    }
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.validation_title), msg,
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
                case ErrorCode.SERVER_OVERLOAD:
                    if(getBaseActivity() != null){
                        ActivitiesWorkFlow.showOverLoadErrorActivity(getBaseActivity());
                        showFragmentErrorRetry();
                    }
                    return true;
                default:
                    break;
            }
        }
        // Case unexpected error from server data
        else if (errorCode == ErrorCode.ERROR_PARSING_SERVER_DATA) {
            showFragmentMaintenance();
        }

        /**
         * TODO: CREATE A METHOD TO DO SOMETHING WHEN IS RECEIVED THE ERROR CUSTOMER_NOT_LOGGED_IN
         * // CODE_CUSTOMER_NOT_LOGGED_IN should be an ErrorCode
         * // CASE REQUEST_ERROR && CUSTOMER_NOT_LOGGED_IN
         */

        return false;
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
        // Case error button
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
        if (view.getId() == R.id.fragment_root_error_button) {
            int error = (int) view.getTag(R.id.fragment_root_error_button);

            if (error == ErrorLayoutFactory.NO_NETWORK_LAYOUT) {
                // Case retry button from network
                onClickRetryNoNetwork(view);
            } else if (error == ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT) {
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
     * validate if it show regular warning or confirmation cart message
     */
    protected void showAddToCartCompleteMessage(BaseResponse baseResponse, boolean isProductBundle){
        //if has cart popup, show configurable confirmation message with cart total price
        if(CountryPersistentConfigs.hasCartPopup(getBaseActivity().getApplicationContext())){
            PurchaseEntity purchaseEntity = ((ShoppingCartAddItemHelper.AddItemStruct) baseResponse.getMetadata().getData()).getPurchaseEntity();
            getBaseActivity().mConfirmationCartMessageView.showMessage(purchaseEntity.getTotal());
        }
        else{
            //show regular message add item to cart
            if(!isProductBundle)
                showInfoAddToShoppingCartCompleted();
            else
                showInfoAddBundleToShoppingCartCompleted();
        }
    }

}
