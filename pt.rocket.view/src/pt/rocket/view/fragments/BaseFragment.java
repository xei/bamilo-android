package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public abstract class BaseFragment extends Fragment implements OnActivityFragmentInteraction {
    
    protected static final String TAG = LogTagHelper.create(BaseFragment.class);
    
    public static int FRAGMENT_VALUE_SET_FAVORITE = 100;
    
    public static int FRAGMENT_VALUE_REMOVE_FAVORITE = 101;
    
    private static Field sChildFragmentManagerField;

	public static final Boolean IS_NESTED_FRAGMENT = true;
	
    public static final Boolean ISNT_NESTED_FRAGMENT = false;

    public static enum KeyboardState { NO_ADJUST_CONTENT, ADJUST_CONTENT };
    
    public static final int NO_INFLATE_LAYOUT = 0;

    private NavigationAction action;

    protected DialogFragment dialog;

    protected Set<MyMenuItem> enabledMenuItems;

    private int mInflateLayoutResId = NO_INFLATE_LAYOUT;
    
    private int titleResId;
    
    private int checkoutStep;
    
    private Boolean isNestedFragment = false;
    
    private boolean isOrderSummaryPresent;
    
    private int ORDER_SUMMARY_CONTAINER = R.id.order_summary_container;
    
    protected boolean isOnStoppingProcess = true;

    private KeyboardState adjustState = KeyboardState.ADJUST_CONTENT;
    
    protected static BaseActivity mainActivity;

    private View mLoadingView;

    private View mEmptyView;

    private View mRetryView;

    private View mContentView;

    private View mFallBackView;

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
     * @param checkoutStep
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
        if (hasLayoutToInflate()) {
            Log.i(TAG, "ON CREATE VIEW: HAS LAYOUT TO INFLATE");
            View view = inflater.inflate(R.layout.fragment_root_layout, container, false);
            ViewStub contentContainer = (ViewStub) view.findViewById(R.id.content_container);
            contentContainer.setLayoutResource(mInflateLayoutResId);
            this.mContentView = contentContainer.inflate();
            return view;
        } else {
            Log.i(TAG, "ON CREATE VIEW: NO HAS LAYOUT TO INFLATE");
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ON VIEW CREATED");
        // Set flag for requests
        isOnStoppingProcess = false;
        // Exist order summary
        isOrderSummaryPresent = (view.findViewById(ORDER_SUMMARY_CONTAINER) != null) ? true : false;
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
    }
    
    /**
     * Show the summary order if the view is present
     * @author sergiopereira
     */
    public void showOrderSummaryIfPresent(int checkoutStep, OrderSummary orderSummary){
        // Get order summary
        if(isOrderSummaryPresent) {
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
        
        // Hide search component for change country
        if(this.action == NavigationAction.Country){
            // Hide search component
            getBaseActivity().hideActionBarItemsForChangeCountry(EnumSet.noneOf(MyMenuItem.class));
            // getBaseActivity().updateActionForCountry(this.action);
        }
        
        // Update base components, like items on action bar
        if (!isNestedFragment && enabledMenuItems != null) {
            Log.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems.toString() + " " + action.toString());
            getBaseActivity().updateBaseComponents(enabledMenuItems, action, titleResId, checkoutStep);
        }
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
            if (getBaseActivity() != null) {
                getBaseActivity().closeDrawerIfOpen();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockActivity#onPause()
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
        if (getView() != null)
            unbindDrawables(getView());
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
            }, 500);
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
        if (getView() != null && isHidden())
            unbindDrawables(getView());

        //System.gc();
    }

    /**
     * Recycle bitmaps
     * 
     * @param view
     * @see http 
     *      ://stackoverflow.com/questions/10314527/caused-by-java-lang-outofmemoryerror-bitmap-size
     *      -exceeds-vm-budget
     *      http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-
     *      bitmap-size-exceeds-vm-budget-android
     */
    public void unbindDrawables(View view) {
        Log.i(TAG, "UNBIND DRAWABLES");
        try {

            if (view.getBackground() != null)
                view.getBackground().setCallback(null);
            else if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                if (view instanceof AdapterView<?>)
                    return;
                
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
     * @see pt.rocket.utils.BaseActivity.OnActivityFragmentInteraction#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        return false;
    }
    
  
    /**
     * #### TRIGGER EVENT ####
     */

    /**
     * 
     * @param type
     */
    protected final void triggerContentEventWithNoLoading(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }

    /**
     * 
     * @param helper
     * @param args
     * @param responseCallback
     */
    protected final void triggerContentEvent(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        showFragmentLoading();
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
        // Hide fall back for each fragment request
        if(getBaseActivity() != null) getBaseActivity().hideMainFallBackView();
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

    /**
     * Interface to communicate with the activity If another type of fragment is created, add the
     * identifier to {@link FragmentType}
     * 
     * @author manuelsilva
     * 
     */
    public interface OnFragmentActivityInteraction {
        public void onFragmentSelected(FragmentType fragmentIdentifier);

        public void onFragmentElementSelected(int position);

        public void sendClickListenerToActivity(OnClickListener clickListener);

        public void sendValuesToActivity(int identifier, Object values);
    }

    public void sendValuesToFragment(int identifier, Object values) {
    }

    public void sendPositionToFragment(int position) {
    }

    public void sendListener(int identifier, OnClickListener clickListener) {
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
     * @return BaseActivity or null
     */
    public BaseActivity getBaseActivity(){
        if(mainActivity == null)
            mainActivity = (BaseActivity) getActivity();
        return mainActivity;
    }

    public void onFragmentSelected(FragmentType type) {
        
    }

    public void onFragmentElementSelected(int position) {
        
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
//        Log.i(TAG, "code1removing callback from request type : "+ bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" size is : "+JumiaApplication.INSTANCE.responseCallbacks.size());
//        Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
//            Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestComplete(bundle);
        }
        JumiaApplication.INSTANCE.getRequestsRetryHelperList().remove((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.getRequestsRetryBundleList().remove((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.getRequestsResponseList().remove((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
        
        // TODO : Validate recover
        JumiaApplication.INSTANCE.getRequestOrderList().remove((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
    }

    /**
     * Handles error responses
     * 
     * @param bundle
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
//        Log.i(TAG, "code1removing callback from request type : "+ bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
//        Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
//            Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestError(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
    }
    
    
    /**
     * Method used to redirect the native checkout to the old checkout method
     * @param activity
     * @author sergiopereira
     */
    public void gotoOldCheckoutMethod(BaseActivity activity, String email, String error){
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
    protected void removeNativeCheckoutFromBackStack(){
        // Native Checkout
        FragmentType[] type = { FragmentType.CHECKOUT_THANKS,   FragmentType.MY_ORDER,      FragmentType.PAYMENT_METHODS,
                                FragmentType.SHIPPING_METHODS,  FragmentType.MY_ADDRESSES,  FragmentType.CREATE_ADDRESS,
                                FragmentType.EDIT_ADDRESS,      FragmentType.POLL,          FragmentType.ABOUT_YOU };
        // Remove tags
        for (FragmentType fragmentType : type)  FragmentController.getInstance().removeAllEntriesWithTag(fragmentType.toString());
    }
    
    
    /**
     * Check the array has content
     * @param array
     * @return true or false
     * @author sergiopereira
     */
    protected boolean hasContent(ArrayList<?> array){
        return (array != null && !array.isEmpty()) ? true : false;
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
     * @return true/false
     * @author sergiopereira
     */
    private boolean hasLayoutToInflate(){
        return (mInflateLayoutResId > NO_INFLATE_LAYOUT) ? true : false;
    }

    /**
     * Show the content fragment from the root layout
     */
    protected void showFragmentContentContainer() {
        setVisibility(mContentView, true);
        setVisibility(mEmptyView, false);
        setVisibility(mRetryView, false);
        setVisibility(mFallBackView, false);
        hideLoadingInfo(mLoadingView);
    }

    /**
     * Show the retry view from the root layout
     * @param listener button
     * @author sergiopereira
     */
    protected void showFragmentRetry(OnClickListener listener) {
        setVisibility(mContentView, false);
        setVisibility(mEmptyView, false);
        hideLoadingInfo(mLoadingView);
        setVisibility(mFallBackView, false);
        setVisibility(mRetryView, true);
        // Set view
        try {
            ((Button) getView().findViewById(R.id.fragment_root_retry_button)).setOnClickListener(listener);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }
    
    /**
     * Show the loading view from the root layout
     */
    protected void showFragmentLoading() {
        setVisibility(mContentView, false);
        setVisibility(mEmptyView, false);
        setVisibility(mRetryView, false);
        setVisibility(mFallBackView, false);
        showLoadingInfo(mLoadingView);
    }
    
    /**
     * Show the empty view from the root layout
     * 
     * @param emptyStringResId string id
     * @param emptyDrawableResId drawable id
     */
    protected void showFragmentEmpty(int emptyStringResId, int emptyDrawableResId) {
        setVisibility(mContentView, false);
        setVisibility(mRetryView, false);
        hideLoadingInfo(mLoadingView);
        setVisibility(mFallBackView, false);
        setVisibility(mEmptyView, true);
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
     * Hide all root views
     */
    protected void hideFragmentRootViews() {
        setVisibility(mContentView, false);
        setVisibility(mEmptyView, false);
        hideLoadingInfo(mLoadingView);
        setVisibility(mRetryView, false);
        setVisibility(mFallBackView, false);
    }

    /**
     * Show the fall back view from the root layout
     */
    protected void showFragmentFallBack() {
        setVisibility(mFallBackView, true);
        setVisibility(mContentView, false);
        setVisibility(mEmptyView, false);
        hideLoadingInfo(mLoadingView);
        setVisibility(mRetryView, false);
    }

    /**
     * Hides the loading screen that appears on the front of the fragmetn while it waits for the
     * data to arrive from the server
     */
    protected final void hideLoadingInfo(View mLoadingView) {
        Log.w(TAG, "HIDDING LAODING LAYOUT");
        // Set view
        try {
            ((LoadingBarView) getView().findViewById(R.id.fragment_root_loading_gif)).stopRendering();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW LOADING LAYOUT");
        }
        setVisibility(mLoadingView, false);
    }
    
    /**
     * Shows the loading screen that appears on the front of the fragment while it waits for the
     * data to arrive from the server
     */
    protected final void showLoadingInfo(View mLoadingView) {
        Log.w(TAG, "SHOWING LAODING LAYOUT");
        setVisibility(mLoadingView, true);
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

    /**
     * Set the visibility
     * @param view
     * @param show
     */
    private final void setVisibility(View view, boolean show) {
        if (view != null) view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    /**
     * ########### INPUT FORMS ########### 
     */
    
    private Locale mLocale;
    
    /**
     * Force input align to left
     * @see 
     * {@link CheckoutAboutYouFragment#onResume()} <br> 
     * {@link SessionLoginFragment#onResume()}
     * @author sergiopereira
     */
    protected void forceInputAlignToLeft(){
        // Save the default locale
        mLocale = Locale.getDefault();
        // Force align to left
        Locale.setDefault(Locale.US);
    }
    
    /**
     * Restore the saved locale {@link #onResume()} if not null.
     * @author sergiopereira
     */
    protected void restoreInputLocale(){
        if(mLocale != null) Locale.setDefault(mLocale);
    }
    
    /**
     * ########### NEXT ########### 
     */

}
