package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
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
import android.widget.ImageView;

import com.facebook.Request;
import com.facebook.Session;
import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.Darwin;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.objects.TeaserGroupType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.service.IRemoteServiceCallback;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.BaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.OnActivityFragmentInteraction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.maintenance.MaintenancePage;
import com.mobile.utils.social.FacebookHelper;
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

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 */
public abstract class BaseFragment extends Fragment implements OnActivityFragmentInteraction, OnClickListener, ViewStub.OnInflateListener {

    protected static final String TAG = LogTagHelper.create(BaseFragment.class);

    public static final int RESTART_FRAGMENTS_DELAY = 500;

    // private static Field sChildFragmentManagerField;

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

    private ViewStub mEmptyView;

    private ViewStub mRetryView;

    private View mContentView;

    private ViewStub mFallBackView;

    private ViewStub mErrorView;

    private ViewStub mMaintenanceView;

    protected long mLoadTime = 0l; // For tacking

    private Locale mLocale;

    public enum KeyboardState {NO_ADJUST_CONTENT, ADJUST_CONTENT}

    private KeyboardState adjustState = KeyboardState.ADJUST_CONTENT;

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
            Log.i(TAG, "ON CREATE VIEW: HAS LAYOUT TO INFLATE");
            View view = inflater.inflate(R.layout.fragment_root_layout, container, false);
            ViewStub contentContainer = (ViewStub) view.findViewById(R.id.content_container);
            contentContainer.setLayoutResource(mInflateLayoutResId);
            this.mContentView = contentContainer.inflate();
            return view;
        } else {
            Log.i(TAG, "ON CREATE VIEW: HAS NO LAYOUT TO INFLATE");
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
        Log.d(TAG, "ON VIEW CREATED");
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
        // Get empty layout
        mEmptyView = (ViewStub) view.findViewById(R.id.fragment_stub_empty);
        mEmptyView.setOnInflateListener(this);
        // Get retry layout
        mRetryView = (ViewStub) view.findViewById(R.id.fragment_stub_retry);
        mRetryView.setOnInflateListener(this);
        // Get fall back layout
        mFallBackView = (ViewStub) view.findViewById(R.id.fragment_stub_home_fall_back);
        mFallBackView.setOnInflateListener(this);
        // Get fall back layout
        mErrorView = (ViewStub) view.findViewById(R.id.fragment_stub_unexpected_error);
        mErrorView.setOnInflateListener(this);
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
            Log.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems.toString() + " " + action.toString());
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
            Log.i(TAG, "ORDER SUMMARY IS PRESENT");
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(ORDER_SUMMARY_CONTAINER, CheckoutSummaryFragment.getInstance(checkoutStep, orderSummary));
            ft.commit();
        } else {
            Log.i(TAG, "ORDER SUMMARY IS NOT PRESENT");
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
        Log.d(TAG, "ON RESUME");

        isOnStoppingProcess = false;

        /**
         * Register service callback
         */
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);

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
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);
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
        Log.w(TAG, "IMPORTANT DATA IS NULL - GOTO HOME -> " + mainActivity.toString());

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
            Log.w(TAG, "RESTART ALL FRAGMENTS - ERROR : Activity is NULL");
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
        Log.i(TAG, "ON LOW MEMORY");

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
        Log.i(TAG, "UNBIND DRAWABLES");
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
            Log.w(TAG, "" + e);
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
        // No intercept the back pressed
        return false;
    }

    /**
     * #### TRIGGER EVENT ####
     */

    /**
     * Send request
     */
    protected final void triggerContentEventNoLoading(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * Send request and show loading
     */
    protected final void triggerContentEvent(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Show loading
        showFragmentLoading();
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * Send request and show progress view
     */
    protected final void triggerContentEventProgress(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        showActivityProgress();
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
        Log.d(TAG, "DYNAMIC FORMS: HIDE KEYBOARD");
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

    // TODO : Validate if is necessary
    /**
     * FIXES 
     * FATAL EXCEPTION: main
     * java.lang.IllegalStateException: No activity
     * see (http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to)
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("ChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "Error getting ChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }
     */

    @Override
    public void onDetach() {
        super.onDetach();
        /**
        // TODO : Validate if is necessary
        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Log.e(TAG, "Error setting mChildFragmentManager field", e);
            }
        }
         */
    }

    /**
     * Callback which deals with the IRemoteServiceCallback
     */
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

        @Override
        public void getError(Bundle response) throws RemoteException {
            Log.i(TAG, "Set target to handle error");
            handleError(response);
        }

        @Override
        public void getResponse(Bundle response) throws RemoteException {
            handleResponse(response);
        }
    };

    /**
     * Handles correct responses
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        // Log.i(TAG, "code1removing callback from request type : "+ bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" size is : "+JumiaApplication.INSTANCE.responseCallbacks.size());
        // Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            // Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestComplete(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
    }

    /**
     * Handles error responses
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        // Log.i(TAG, "code1removing callback from request type : "+ bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        // Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            // Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestError(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
    }

    /**
     * Method used to redirect the native checkout to the old checkout method
     */
    public void gotoOldCheckoutMethod(BaseActivity activity, String email, String error) {
        Log.w(TAG, "WARNING: GOTO WEB CHECKOUT");
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
                Log.d(TAG, "UPDATE ADJUST STATE: " + stateString);
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
        UIUtils.showOrHideViews(View.GONE, mLoadingView, mEmptyView, mRetryView, mErrorView, mFallBackView, mMaintenanceView);
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentNoNetworkRetry() {
        UIUtils.showOrHideViews(View.VISIBLE, mRetryView);
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
        Log.i(TAG, "ON SHOW CONTINUE LAYOUT");
        showFragmentEmpty(R.string.server_error, R.drawable.img_warning, R.string.continue_shopping, this);
    }

    /**
     * Show the empty view from the root layout
     * @param emptyStringResId The string id for title
     * @param emptyDrawableResId The drawable id for image
     * @param buttonEmptyStringResId The string id for button
     * @param onClickListener The click listener
     */
    protected void showFragmentEmpty(int emptyStringResId, int emptyDrawableResId, int buttonEmptyStringResId, OnClickListener onClickListener) {
        // Set view with some data
        mEmptyView.setTag(R.id.stub_text_title, emptyStringResId);
        mEmptyView.setTag(R.id.stub_drawable, emptyDrawableResId);
        mEmptyView.setTag(R.id.stub_text_button, buttonEmptyStringResId);
        mEmptyView.setTag(R.id.stub_listener, onClickListener);
        // Show empty view
        UIUtils.showOrHideViews(View.VISIBLE, mEmptyView);
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentErrorRetry() {
        UIUtils.showOrHideViews(View.VISIBLE, mErrorView);
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
        // Case continue shopping
        if(id == R.id.fragment_stub_empty) {
            onInflateContinue(stub, inflated);
        }
        // Case home fall back
        else if(id == R.id.fragment_stub_home_fall_back)  {
            onInflateHomeFallBack(inflated);
        }
        // Case loading
        else if(id == R.id.fragment_stub_loading) {
            onInflateLoading();
        }
        // Case no network
        else if(id == R.id.fragment_stub_retry) {
            onInflateNoNetwork(inflated);
        }
        // Case unexpected error
        else if(id == R.id.fragment_stub_unexpected_error) {
            onInflateUnexpectedError(inflated);
        }
        // Case maintenance
        else if(id == R.id.fragment_stub_maintenance) {
            onInflateMaintenance(inflated);
        }
        // Case unknown
        else {
            Log.w(TAG, "WARNING: UNKNOWN INFLATED STUB");
        }
    }

    /**
     * Set the continue view after inflate stub.
     * @param stub The continue stub
     * @param inflated The inflated view
     */
    private void onInflateContinue(ViewStub stub, View inflated) {
        Log.i(TAG, "ON INFLATE STUB: EMPTY");
        // Get associated data
        int emptyStringResId = (int) stub.getTag(R.id.stub_text_title);
        int emptyDrawableResId = (int) stub.getTag(R.id.stub_drawable);
        int buttonEmptyStringResId = (int) stub.getTag(R.id.stub_text_button);
        OnClickListener onClickListener = (OnClickListener) stub.getTag(R.id.stub_listener);
        // Set view
        ((ImageView) inflated.findViewById(R.id.fragment_root_empty_image)).setImageResource(emptyDrawableResId);
        ((TextView) inflated.findViewById(R.id.fragment_root_empty_text)).setText(getString(emptyStringResId));
        Button emptyContinueButton = (Button) inflated.findViewById(R.id.fragment_root_empty_button);
        emptyContinueButton.setVisibility(View.VISIBLE);
        emptyContinueButton.setText(getString(buttonEmptyStringResId));
        emptyContinueButton.setOnClickListener(onClickListener);
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mRetryView, mErrorView, mFallBackView, mMaintenanceView, mLoadingView);
    }

    /**
     * Set the home fall back view.
     */
    private void onInflateHomeFallBack(View inflated) {
        Log.i(TAG, "ON INFLATE STUB: FALL BACK");
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
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView, mMaintenanceView, mLoadingView);
    }

    /**
     * Set the loading view.
     */
    private void onInflateLoading() {
        Log.i(TAG, "ON INFLATE STUB: LOADING");
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView, mFallBackView, mMaintenanceView);
    }

    /**
     * Set no network view.
     * @param inflated The inflated view
     */
    private void onInflateNoNetwork(View inflated) {
        Log.i(TAG, "ON INFLATE STUB: RETRY");
        // Set view
        inflated.findViewById(R.id.fragment_root_retry_network).setOnClickListener(this);
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mErrorView, mFallBackView, mMaintenanceView, mLoadingView);
    }
    
    /**
     * Set unexpected error view.
     * @param inflated The inflated view
     */
    private void onInflateUnexpectedError(View inflated) {
        Log.i(TAG, "ON INFLATE STUB: UNEXPECTED ERROR");
        inflated.findViewById(R.id.fragment_root_retry_unexpected_error).setOnClickListener(this);
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mFallBackView, mRetryView, mMaintenanceView, mLoadingView);
    }

    /**
     * Set maintenance view.
     * @param inflated The inflated view
     */
    private void onInflateMaintenance(View inflated) {
        Log.i(TAG, "ON INFLATE STUB: UNEXPECTED ERROR");
        // Validate venture
        if (getResources().getBoolean(R.bool.is_bamilo_specific)) {
            MaintenancePage.setMaintenancePageBamilo(inflated, this);
        } else {
            MaintenancePage.setMaintenancePageBaseActivity(getBaseActivity(), this);
        }
        // Hide other stubs
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView, mFallBackView, mLoadingView);
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
        Log.i(TAG, "ON HANDLE SUCCESS EVENT");
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
                Log.i(TAG, "LOGOUT EVENT");
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
        Log.i(TAG, "ON HANDLE ERROR EVENT");

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        EventTask eventTask = (EventTask) bundle.getSerializable(Constants.BUNDLE_EVENT_TASK);

        if (!bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY)) {
            return false;
        }

        if (errorCode == null) {
            return false;
        }

        Log.i(TAG, "ON HANDLE ERROR EVENT: " + errorCode.toString());
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
            default:
                break;
            }
        }
        return false;

    }

    protected void clearCredentials() {
        JumiaApplication.INSTANCE.setLoggedIn(false);
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        getBaseActivity().updateNavigationMenu();
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
        // Case retry button from network
        if (id == R.id.fragment_root_retry_network) onClickRetryNoNetwork(view);
        // Case retry button from error
        else if(id == R.id.fragment_root_retry_unexpected_error) onClickRetryUnexpectedError(view);
        // Case retry button in maintenance page
        else if(id == R.id.fragment_root_retry_maintenance)  onClickRetryMaintenance(view);
        // Case continue button
        else if(id == R.id.fragment_root_empty_button) onClickContinueButton();
        // Case choose country button in maintenance page
        else if(id == R.id.fragment_root_cc_maintenance) onClickMaintenanceChooseCountry();
        // Case unknown
        else Log.w(TAG, "WARNING: UNKNOWN CLICK EVENT");
    }

    /**
     * Process the click on retry button in no network.
     * @param view The clicked view
     */
    protected void onClickRetryNoNetwork(View view) {
        try {
            Animation animation = AnimationUtils.loadAnimation(getBaseActivity(), R.anim.anim_rotate);
            view.findViewById(R.id.fragment_root_retry_spinning).clearAnimation();
            view.findViewById(R.id.fragment_root_retry_spinning).setAnimation(animation);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
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
            view.findViewById(R.id.fragment_root_retry_spinning).clearAnimation();
            view.findViewById(R.id.fragment_root_retry_spinning).setAnimation(animation);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
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
        Log.i(TAG, "USER NOT ACCEPT THE SECOND FACEBOOK DIALOG");
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
        Log.i(TAG, "USER NOT ACCEPT EMAIL PERMISSION");
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
        Log.i(TAG, "USER ACCEPT PERMISSIONS");
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
     *
     * @param targetUrl
     * @param bundle
     * @author sergiopereira
     */
    protected void onClickProduct(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK PRODUCT");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Log.i(TAG, "WARNING: URL IS NULL");
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
     * Process the category click
     *
     * @param targetUrl
     * @param bundle
     * @author sergiopereira
     */
    protected void onClickCategory(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK CATEGORY");
        bundle.putString(ConstantsIntentExtra.CATEGORY_URL, targetUrl);
        getBaseActivity().onSwitchFragment(FragmentType.CATEGORIES, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the catalog click
     *
     * @param targetUrl
     * @param targetTitle
     * @param bundle
     */
    protected void onClickCatalog(String targetUrl, String targetTitle, Bundle bundle) {
        Log.i(TAG, "ON CLICK CATALOG");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaser_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, true);
        } else {
            Log.w(TAG, "WARNING: URL IS NULL");
        }
    }



    /**
     * Process the brand click
     *
     * @param targetUrl
     * @param bundle
     */
    protected void onClickBrand(String targetUrl, Bundle bundle) {
        Log.i(TAG, "ON CLICK BRAND");
        if (targetUrl != null) {
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetUrl);
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, targetUrl);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Log.i(TAG, "WARNING: URL IS NULL");
        }
    }

    /**
     * Process the campaign click.
     *
     * @param view
     * @param targetUrl
     * @param targetTitle
     * @param bundle
     */
    protected void onClickCampaign(View view, TeaserGroupType origin, String targetUrl, String targetTitle, Bundle bundle) {
    }

    /**
     * Create an array with a single campaign
     *
     * @param targetTitle
     * @param targetUrl
     * @return ArrayList with one campaign
     * @author sergiopereira
     */
    protected ArrayList<TeaserCampaign> createSignleCampaign(String targetTitle, String targetUrl) {
        ArrayList<TeaserCampaign> campaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(targetTitle);
        campaign.setUrl(targetUrl);
        campaigns.add(campaign);
        return campaigns;
    }

}