package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public abstract class BaseFragment extends Fragment implements OnActivityFragmentInteraction {

    private static final Set<EventType> HANDLED_EVENTS = EnumSet.noneOf(EventType.class);
    protected String md5Hash = null;
    
    private static String writeReviewFragment = "pt.rocket.view.WriteReviewFragmentActivity";
    private static Field sChildFragmentManagerField;

	public static final Boolean IS_NESTED_FRAGMENT = true;
    public static final Boolean ISNT_NESTED_FRAGMENT = false;

    private static final boolean VISIBLE = true;

    private static final boolean NOT_VISIBLE = false;


	protected static final String TAG = LogTagHelper.create(BaseFragment.class);

    protected View contentContainer;

    private NavigationAction action;

    private Set<EventType> contentEvents;

    protected DialogFragment dialog;

    private final Set<EventType> allHandledEvents = EnumSet.copyOf(HANDLED_EVENTS);

    private Set<EventType> userEvents;

    protected Set<MyMenuItem> enabledMenuItems;

    private int titleResId;
   
    private Boolean isNestedFragment = false;

    private boolean isVisible = false;
    
    private boolean isOrderSummaryPresent;
    
    private int ORDER_SUMMARY_CONTAINER = R.id.order_summary_container;

    public BaseFragment() {
    }
    protected static BaseActivity mainActivity;
    
    /**
     * Constructor
     */
    public BaseFragment(Set<EventType> contentEvents, Set<EventType> userEvents,
            Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId) {
        this.contentEvents = contentEvents;
        this.userEvents = userEvents;
        this.allHandledEvents.addAll(contentEvents);
        this.allHandledEvents.addAll(userEvents);
        this.enabledMenuItems = enabledMenuItems;
        this.action = action;
        this.titleResId = titleResId;
    }

    /**
     * Constructor used only by nested fragments
     * 
     * @param isNestedFragment
     */
    public BaseFragment(Boolean isNestedFragment) {
        this.isNestedFragment = isNestedFragment;
    }
    
    /**
     * Constructor used only by nested fragments
     * 
     * @param isNestedFragment
     */
    public BaseFragment(Boolean isNestedFragment, NavigationAction action) {
        this.isNestedFragment = isNestedFragment;
        this.action = action;
    }


    /**
     * #### LIFE CICLE ####
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
        md5Hash = null;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ON VIEW CREATED");
        // Exist order summary
        isOrderSummaryPresent = (view.findViewById(ORDER_SUMMARY_CONTAINER) != null) ? true : false;
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

        setVisiblility(VISIBLE);
        
        if(this.action == NavigationAction.Country){
            ((BaseActivity) getActivity()).updateActionForCountry(this.action);
        }
        
        // Validate if is checkout process
        if(action == NavigationAction.Checkout) enabledMenuItems = getCheckoutMenuItem();
        
        // Update base components, like items on action bar
        if (!isNestedFragment && enabledMenuItems != null) {
            Log.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems.toString() + " " + action.toString());
            ((BaseActivity) getActivity()).updateBaseComponents(enabledMenuItems, action, titleResId);
        }
    }

    /**
     * XXX 
     * @return
     */
    private static Set<MyMenuItem> getCheckoutMenuItem(){
        return (BaseActivity.isTabletInLandscape(JumiaApplication.INSTANCE)) ? EnumSet.of(MyMenuItem.SEARCH_BAR) : EnumSet.of(MyMenuItem.SEARCH);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        
        /**
         * Register service callback
         */
        JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
        
        if(((BaseActivity) getActivity()) != null){
            ((BaseActivity) getActivity()).showWarning(false);
            ((BaseActivity) getActivity()).showWarningVariation(false);
        }
        Log.d(getTag(), "onResume");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        // Save the current state
        setVisiblility(NOT_VISIBLE);
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
        
        BaseActivity activity = getBaseActivity();
        //if ( null == activity )
        //    activity = mainActivity;
        
        if ( null != activity) {
            activity.showLoading(false);
            activity.onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
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

        System.gc();
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
        sendRequest(helper, args, responseCallback);
    }

    protected final void triggerContentEvent(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        ((BaseActivity) getActivity()).showLoading(false);
        sendRequest(helper, args, responseCallback);
    }

//    protected final void triggerContentEvent(RequestEvent event) {
//        ((BaseActivity) getActivity()).showLoading(false);
//        EventManager.getSingleton().triggerRequestEvent(event);
//    }

    protected final void triggerContentEventProgress(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        ((BaseActivity) getActivity()).showProgress();
        sendRequest(helper, args, responseCallback);
    }

    private String getFragmentTag() {
        return this.getClass().getSimpleName();
    }
    
    
    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param responseCallback
     * @return the md5 of the reponse
     */
    public String sendRequest(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        Bundle bundle = helper.generateRequestBundle(args);
        if(bundle.containsKey(Constants.BUNDLE_EVENT_TYPE_KEY)){
            Log.i(TAG, "codesave saving : "+(EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
            JumiaApplication.INSTANCE.getRequestsRetryHelperList().put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), helper);
            JumiaApplication.INSTANCE.getRequestsRetryBundleList().put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), args);
            JumiaApplication.INSTANCE.getRequestsResponseList().put((EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY), responseCallback);
        } else {
            Log.w(TAG, " MISSING EVENT TYPE from "+helper.toString());
        }
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        Log.d("TRACK", "sendRequest");
        
        JumiaApplication.INSTANCE.responseCallbacks.put(md5, new IResponseCallback() {

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.d("TRACK", "onRequestComplete BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle);
                
                if (responseCallback != null) {
                    if(formatedBundle.getBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY)){
                        responseCallback.onRequestError(formatedBundle);
                    } else {
                        responseCallback.onRequestComplete(formatedBundle);    
                    }
                    
                }
            }

            @Override
            public void onRequestError(Bundle bundle) {
                Log.d("TRACK", "onRequestError  BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.parseErrorBundle(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestError(formatedBundle);
                }
            }
        });

        
        JumiaApplication.INSTANCE.sendRequest(bundle);
        

        return md5;
    }
    /**
     * #### HANDLE EVENT ####
     */
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
//     */
//    @Override
//    public final void handleEvent(final ResponseEvent event) {
//
//        // Validate fragment visibility
//        if (!isOnTheScreen() && event.getType() != EventType.FACEBOOK_LOGIN_EVENT) {
//            Log.w(TAG, "RECEIVED EVENT IN BACKGROUND WAS DISCARDED: " + event.getType().name());
//            return;
//        }
//
//        if (event.getSuccess()) {
//            Log.i(TAG, "HANDLE EVENT: SUCCESS");
//            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
//                boolean showContent = onSuccessEvent((ResponseResultEvent<?>) event);
//                try {
//                    if (showContent) {
//                        ((BaseActivity) getActivity()).showContentContainer(false);
//                    }
//                    ((BaseActivity) getActivity()).showWarning(event.warning != null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                
//                if(getActivity() != null){
//                    ((BaseActivity) getActivity()).showWarning(event.warning != null);                    
//                } else {
//                   return;
//                }
//                
//
//            }
//            handleSuccessEvent(event);
//
//        } else {
//            boolean needsErrorHandling = true;
//            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
//                needsErrorHandling = !onErrorEvent(event);
//            }
//            if (needsErrorHandling) {
//                handleErrorEvent(event);
//            }
//        }
//    }

    /**
     * Handles a successful event and reflects necessary changes on the UI.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     */
    private void handleSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON HANDLE SUCCESS EVENT: " + eventType);
        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            getBaseActivity().updateCartInfo();
            break;
        case LOGOUT_EVENT:
            break;
        }
    }

    /**
     * Handles a failed event and shows dialogs to the user.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     */
    public void handleErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        try {
            HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            List<String> errors = (List<String>) errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
            Log.i(TAG, "ON HANDLE ERROR EVENT: " + eventType);
            if(errors != null)
                Log.i(TAG, "ON HANDLE ERROR EVENT error response was : error code : " + errorCode +" error message : " + errors.toString());
        } catch (NullPointerException e) {
            Log.w(TAG, "ON HANDLE ERROR: The Message is null: " + e.getMessage());
        }


        /*
         * TODO: finish to distinguish between errors else if (event.errorCode.isServerError()) {
         * dialog = DialogGeneric.createServerErrorDialog(MyActivity.this, new OnClickListener() {
         * 
         * @Override public void onClick(View v) { showLoadingInfo();
         * EventManager.getSingleton().triggerRequestEvent(event.request); dialog.dismiss(); } },
         * false); dialog.show(); return; } else if (event.errorCode.isClientError()) { dialog =
         * DialogGeneric.createClientErrorDialog( MyActivity.this, new OnClickListener() {
         * 
         * @Override public void onClick(View v) { showLoadingInfo();
         * EventManager.getSingleton().triggerRequestEvent(event.request); dialog.dismiss(); } },
         * false); dialog.show(); return; }
         */
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * This method generates a unique and always diferent MD5 hash based on a given key 
     * @param key 
     * @return the unique MD5 
     */
    protected static String uniqueMD5(String key) { 
        String md5String = "";
        try {
            Calendar calendar = Calendar.getInstance();
            key = key + calendar.getTimeInMillis() ;
        
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();
     
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            md5String = hexString.toString();
     
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         }
        
        return md5String;
 
    }         
    
    
    /**
     * ######### IN FRONT #########
     */

    /**
     * @return the isVisible
     */
    public boolean isOnTheScreen() {
        return isVisible;
    }

    /**
     * @param isVisible
     *            the isVisible to set
     */
    public void setVisiblility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setActivity(BaseActivity activity) {
        this.mainActivity = activity;
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
    public void gotoOldCheckoutMethod(BaseActivity activity){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.CHECKOUT_BASKET);
        bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN, getString(R.string.mixprop_loginlocationcart));
        activity.onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
}
