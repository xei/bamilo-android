package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.service.IRemoteServiceCallback;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LoadingBarView;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.BaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.OnActivityFragmentInteraction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.lang.reflect.Field;
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
public abstract class BaseFragment extends Fragment implements OnActivityFragmentInteraction, OnClickListener {

    protected static final String TAG = LogTagHelper.create(BaseFragment.class);

    public static final int FRAGMENT_VALUE_SET_FAVORITE = 100;

    public static final int FRAGMENT_VALUE_REMOVE_FAVORITE = 101;

    public static final int RESTART_FRAGMENTS_DELAY = 500;

    private static Field sChildFragmentManagerField;

    public static final Boolean IS_NESTED_FRAGMENT = true;

    public static final Boolean ISNT_NESTED_FRAGMENT = false;

    public enum KeyboardState {NO_ADJUST_CONTENT, ADJUST_CONTENT};

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

    private KeyboardState adjustState = KeyboardState.ADJUST_CONTENT;

    private BaseActivity mainActivity;

    private View mLoadingView;

    private View mEmptyView;

    private View mRetryView;

    private View mContentView;

    private View mFallBackView;

    private View mErrorView;

    // For tacking
    protected long mLoadTime = 0l;

    private Locale mLocale;

    /**
     * Constructor with layout to inflate
     *
     * @param enabledMenuItems
     * @param action
     * @param layoutResId
     * @param titleResId
     * @param adjust_state
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
     *
     * @param isNestedFragment
     * @param layoutResId
     */
    public BaseFragment(Boolean isNestedFragment, int layoutResId) {
        this.isNestedFragment = isNestedFragment;
        this.mInflateLayoutResId = layoutResId;
        this.titleResId = 0;
        this.checkoutStep = ConstantsCheckout.NO_CHECKOUT;
    }

    /**
     * Constructor used only by PDV fragments
     *
     * @param enabledMenuItems
     * @param action
     * @param titleResId
     * @param adjust_state
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
     *
     * @param enabledMenuItems
     * @param action
     * @param layoutResId
     * @param titleResId
     * @param adjust_state
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
        mLoadingView = view.findViewById(R.id.fragment_loading_stub);
        // Get empty layout
        mEmptyView = view.findViewById(R.id.fragment_empty_stub);
        // Get retry layout
        mRetryView = view.findViewById(R.id.fragment_retry_stub);
        // Get fall back layout
        mFallBackView = view.findViewById(R.id.fragment_fall_back_stub);
        // Get fall back layout
        mErrorView = view.findViewById(R.id.fragment_unexpected_error_stub);

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

        if (getBaseActivity() != null) {
            getBaseActivity().showWarning(false);
            getBaseActivity().showWarningVariation(false);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);

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
     *
     * @param view
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
        return false;
    }

    /**
     * #### TRIGGER EVENT ####
     */

    /**
     * @param helper
     * @param args
     * @param responseCallback
     */
    protected final void triggerContentEventNoLoading(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * @param helper
     * @param args
     * @param responseCallback
     */
    protected final void triggerContentEvent(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Show loading
        showFragmentLoading();
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
        // Hide fall back for each fragment request
        if (getBaseActivity() != null) {
            getBaseActivity().hideMainFallBackView();
        }
    }

    /**
     * 
     * @param helper
     * @param args
     * @param responseCallback
     */
    protected final void triggerContentEventProgress(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        showActivityProgress();
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }

    public void sendValuesToFragment(Object values) {
    }

    public void notifyFragment(Bundle bundle) {

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
     */
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Error getting mChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // TODO : Validate if is necessary
        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Log.e(TAG, "Error setting mChildFragmentManager field", e);
            }
        }
    }

    /**
     * Requests and Callbacks methods
     */

    // TODO : VALIDATE THIS

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
     *
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        // Log.i(TAG, "code1removing callback from request type : "+ bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" size is : "+JumiaApplication.INSTANCE.responseCallbacks.size());
        // Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            // Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestComplete(bundle);
        }
        JumiaApplication.INSTANCE.getRequestsRetryHelperList().remove(bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.getRequestsRetryBundleList().remove(bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.getRequestsResponseList().remove(bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);

        // TODO : Validate recover
        JumiaApplication.INSTANCE.getRequestOrderList().remove(bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
    }

    /**
     * Handles error responses
     *
     * @param bundle
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
     *
     * @param activity
     * @author sergiopereira
     */
    public void gotoOldCheckoutMethod(BaseActivity activity, String email, String error) {
        Log.w(TAG, "WARNING: GOTO WEB CHECKOUT");
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.EMAIL_KEY, email);
        params.putString(TrackerDelegator.ERROR_KEY, error);
        TrackerDelegator.trackNativeCheckoutError(params);

        // Warning user
        Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
        // Remove native checkout
        removeNativeCheckoutFromBackStack();
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.CHECKOUT_BASKET);
        activity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Method used to remove all native checkout entries from the back stack on the Fragment Controller
     * Note: Updated this method if you add a new native checkout step
     * @author sergiopereira 
     */
    protected void removeNativeCheckoutFromBackStack() {
        // Native Checkout
        FragmentType[] type = { FragmentType.CHECKOUT_THANKS,   FragmentType.MY_ORDER,      FragmentType.PAYMENT_METHODS,
                                FragmentType.SHIPPING_METHODS,  FragmentType.MY_ADDRESSES,  FragmentType.CREATE_ADDRESS,
                                FragmentType.EDIT_ADDRESS,      FragmentType.POLL,          FragmentType.ABOUT_YOU };
        // Remove tags
        for (FragmentType fragmentType : type) FragmentController.getInstance().removeAllEntriesWithTag(fragmentType.toString());
    }

    /**
     * Check the array has content
     * 
     * @param array
     * @return true or false
     * @author sergiopereira
     */
    protected boolean hasContent(ArrayList<?> array) {
        return array != null && !array.isEmpty();
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

    /**
     * ########### ROOT LAYOUT ###########
     */

    /**
     * Validate if is to inflate a fragment layout into root layout
     *
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
        UIUtils.showOrHideViews(View.GONE, mEmptyView, mRetryView, mErrorView, mFallBackView);
        hideLoadingInfo(mLoadingView);
    }

    /**
     * Show the retry view from the root layout
     *
     * @param listener button
     * @author sergiopereira
     */
    protected void showFragmentNoNetworkRetry(final OnClickListener listener) {
        UIUtils.showOrHideViews(View.VISIBLE, mRetryView);
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mErrorView, mFallBackView);
        hideLoadingInfo(mLoadingView);
        // Set view
        try {
            (getView().findViewById(R.id.fragment_root_retry_button)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    Animation animation = AnimationUtils.loadAnimation(BaseFragment.this.getActivity(), R.anim.anim_rotate);
                    getView().findViewById(R.id.fragment_root_retry_spinning).setAnimation(animation);
                }
            });
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }

    /**
     * Show the retry view to try to execute the event again
     *
     * @param eventType
     * @author ricardosoares
     */
    protected void showFragmentNoNetworkRetry(final EventType eventType) {
        showFragmentNoNetworkRetry(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryRequest(eventType);
            }
        });
    }

    /**
     * Show the loading view from the root layout
     */
    protected void showFragmentLoading() {
        showLoadingInfo(mLoadingView);
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView, mFallBackView);
    }

    /**
     * Show the empty view from the root layout
     * 
     * @param emptyStringResId
     *            string id
     * @param emptyDrawableResId
     *            drawable id
     */
    protected void showFragmentEmpty(int emptyStringResId, int emptyDrawableResId) {
        UIUtils.showOrHideViews(View.VISIBLE, mEmptyView);
        UIUtils.showOrHideViews(View.GONE, mContentView, mRetryView, mErrorView, mFallBackView);
        hideLoadingInfo(mLoadingView);
        // Set view
        try {
            ((ImageView) getView().findViewById(R.id.fragment_root_empty_image)).setImageResource(emptyDrawableResId);
            ((TextView) getView().findViewById(R.id.fragment_root_empty_text)).setText(getString(emptyStringResId));
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW EMPTY LAYOUT");
        }
    }

    /**
     * Show the empty view from the root layout
     *
     * @param emptyStringResId
     * @param emptyDrawableResId
     * @param buttonEmptyStringResId
     * @param onClickListener
     * @author Andre Lopes
     */
    protected void showFragmentEmpty(int emptyStringResId, int emptyDrawableResId, int buttonEmptyStringResId, OnClickListener onClickListener) {
        showFragmentEmpty(emptyStringResId, emptyDrawableResId);
        // Set view
        try {
            Button emptyContinueButton = (Button) getView().findViewById(R.id.fragment_root_empty_button);
            emptyContinueButton.setVisibility(View.VISIBLE);
            emptyContinueButton.setText(getString(buttonEmptyStringResId));
            emptyContinueButton.setOnClickListener(onClickListener);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW EMPTY LAYOUT");
        }
    }

    /**
     * Show the retry view from the root layout
     *
     * @author sergiopereira
     */
    protected void showFragmentErrorRetry() {
        UIUtils.showOrHideViews(View.VISIBLE, mErrorView);
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mFallBackView, mRetryView);
        hideLoadingInfo(mLoadingView);
        // Set view
        try {
            (getView().findViewById(R.id.fragment_root_error_button)).setOnClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }


    /**
     * Show continue
     *
     * @author sergiopereira
     */
    protected void showContinueShopping(OnClickListener listener) {
        Log.i(TAG, "ON SHOW CONTINUE LAYOUT");
        showFragmentEmpty(R.string.server_error, R.drawable.img_warning, R.string.continue_shopping, listener);
    }


    /**
     * Show continue with listener for going back.
     *
     * @author sergiopereira
     */
    protected void showContinueShopping() {
        Log.i(TAG, "ON SHOW CONTINUE LAYOUT");
        showFragmentEmpty(R.string.server_error, R.drawable.img_warning, R.string.continue_shopping, this);
    }

    /**
     * Hide all root views
     */
    protected void hideFragmentRootViews() {
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView, mFallBackView);
        hideLoadingInfo(mLoadingView);
    }

    /**
     * Show the fall back view from the root layout
     */
    protected void showFragmentFallBack() {
        UIUtils.showOrHideViews(View.VISIBLE, mFallBackView);
        UIUtils.showOrHideViews(View.GONE, mContentView, mEmptyView, mRetryView, mErrorView);
        hideLoadingInfo(mLoadingView);
    }

    /**
     * Hides the loading screen that appears on the front of the fragment while it waits for the
     * data to arrive from the server
     */
    protected final void hideLoadingInfo(View mLoadingView) {
        Log.w(TAG, "HIDING LOADING LAYOUT");
        // Set view
        try {
            ((LoadingBarView) getView().findViewById(R.id.fragment_root_loading_gif)).stopRendering();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW LOADING LAYOUT");
        }
        UIUtils.showOrHideViews(View.GONE, mLoadingView);
    }

    /**
     * Shows the loading screen that appears on the front of the fragment while it waits for the
     * data to arrive from the server
     */
    protected final void showLoadingInfo(View mLoadingView) {
        Log.w(TAG, "SHOWING LOADING LAYOUT");
        UIUtils.showOrHideViews(View.VISIBLE, mLoadingView);
        // Set view
        try {
            ((LoadingBarView) getView().findViewById(R.id.fragment_root_loading_gif)).startRendering();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW LOADING LAYOUT");
        }
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
        getBaseActivity().showWarning(R.string.no_internet_access_warning_title);
        hideActivityProgress();
        showFragmentContentContainer();
    }

    protected void showUnexpectedErrorWarning() {
        getBaseActivity().showWarning(R.string.server_error);
        showFragmentContentContainer();
        hideActivityProgress();
    }

    /**
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

    /**
     * ########### NEXT ###########
     */

    public boolean handleSuccessEvent(Bundle bundle) {
        Log.i(TAG, "ON HANDLE ERROR EVENT");
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

    @SuppressWarnings("unchecked")
    public boolean handleErrorEvent(final Bundle bundle) {
        Log.i(TAG, "ON HANDLE ERROR EVENT");

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
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
                    showFragmentNoNetworkRetry(eventType);
                }
                return true;
            case HTTP_STATUS:
                // Case HOME show retry
                if(action == NavigationAction.Home) showFragmentErrorRetry();
                // Case Default show continue shopping
                else showContinueShopping();
                return true;
            case SSL:
                getBaseActivity().setLayoutMaintenance(eventType, this ,true);
                return true;
            case SERVER_IN_MAINTENANCE:
                // Show maintenance page
                getBaseActivity().setLayoutMaintenance(eventType, this, false);
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
        if (id == R.id.fragment_root_retry_button) onRetryRequest(null);
        // Case continue button
        else if(id == R.id.fragment_root_empty_button) onClickContinueButton();
        // Case retry button from error
        else if(id == R.id.fragment_root_error_button) onClickErrorButton(view);
        // Case retry button in maintenance page
        else if(id == R.id.fallback_retry)  onClickMaintenanceRetryButton(); 
        // Case choose country button in maintenance page
        else if(id == R.id.fallback_change_country) onClickMaintenanceChooseCountry();
        // Case unknown
        else Log.w(TAG, "WARNING: UNKNOWN CLICK EVENT");
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
     * Process the click in continue shopping
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickErrorButton(View view) {
        try {
            Animation animation = AnimationUtils.loadAnimation(BaseFragment.this.getActivity(), R.anim.anim_rotate);
            getView().findViewById(R.id.fragment_root_error_spinning).setAnimation(animation);    
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SET RETRY BUTTON ANIMATION");
        }
    }

    /**
     * Process the click on retry button in maintenance page
     *
     * @modified sergiopereira
     */
    protected void onClickMaintenanceRetryButton() {
        getBaseActivity().hideMainFallBackView();
        onClickErrorButton(null);
    }

    /**
     *
     * @param eventType
     */
    protected void onRetryRequest(EventType eventType) {
        Log.i(TAG, "ON RETRY REQUEST");
        retryLastRequest(eventType);
    }

    /**
     *
     * @param eventType
     * @return
     */
    private String retryLastRequest(EventType eventType) {
        if (eventType != null) {
            return JumiaApplication.INSTANCE.sendRequest(
                    JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(eventType),
                    JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(eventType),
                    JumiaApplication.INSTANCE.getRequestsResponseList().get(eventType));
        }
        return null;
    }

    /**
     * Process the click on choose country button in maintenance page.
     *
     * @author sergiopereira
     */
    private void onClickMaintenanceChooseCountry() {
        // Show Change country
        FragmentController.getInstance().removeEntriesUntilTag(FragmentType.HOME.toString());
        getBaseActivity().onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /*
     * ########### FACEBOOK ###########
     */

    /**
     * Clean the current session and warning user.
     *
     * @author sergiopereira
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
     *
     * @param session
     * @param callback
     * @author sergiopereira
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
     *
     * @param session
     * @param callback
     * @author sergiopereira
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
     * No network dialog for facebook exception handling<br>
     * Deprecated: using no network layout instead
     */
    @Deprecated
    protected final void createNoNetworkDialog(final View clickView) {
        // Validate button
        if (clickView == null) {
            return;
        }
        // Show
        dialog = DialogGenericFragment.createNoNetworkDialog(getActivity(),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickView.performClick();
                        dismissDialogFragment();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialogFragment();
                    }
                }, false);
        try {
            dialog.show(getActivity().getSupportFragmentManager(), null);
        } catch (Exception e) {
            // ...
        }
    }

    /**
     *
     */
    protected void dismissDialogFragment() {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

}