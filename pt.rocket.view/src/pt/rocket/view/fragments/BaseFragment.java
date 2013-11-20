package pt.rocket.view.fragments;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
public abstract class BaseFragment extends Fragment implements ResponseListener,
        OnActivityFragmentInteraction {

    private static final Set<EventType> HANDLED_EVENTS = EnumSet.noneOf(EventType.class);
    protected String md5Hash = null;
    
    private static String writeReviewFragment = "pt.rocket.view.WriteReviewFragmentActivity";

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

    private Set<MyMenuItem> enabledMenuItems;

    private int titleResId;
   
    private Boolean isNestedFragment = false;

    private boolean isVisible = false;

    public BaseFragment() {
    }
    protected BaseActivity mainActivity;
    
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
        EventManager.getSingleton().addResponseListener(this, allHandledEvents);
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
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        // Save the current state
        setVisiblility(VISIBLE);
        // Update base components, like items on action bar
        if (!isNestedFragment && enabledMenuItems != null) {
            Log.i(TAG,
                    "UPDATE BASE COMPONENTS: " + enabledMenuItems.toString() + " "
                            + action.toString());
            ((BaseActivity) getActivity()).updateBaseComponents(enabledMenuItems, action,
                    titleResId);
            // Force resume
            ImageLoader.getInstance().resume();
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
        // Remove listeners
        EventManager.getSingleton().removeResponseListener(this, allHandledEvents);
        // Recycle bitmaps
        if (getView() != null)
            unbindDrawables(getView());
        mainActivity = null;
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
            activity.showLoading();
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
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiscCache();

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
                ((ViewGroup) view).removeAllViews();
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

    protected final void triggerContentEventWithNoLoading(RequestEvent event) {
        EventManager.getSingleton().triggerRequestEvent(event);
    }

    protected final void triggerContentEvent(EventType type) {
        triggerContentEvent(new RequestEvent(type));
    }

    protected final void triggerContentEvent(RequestEvent event) {
        ((BaseActivity) getActivity()).showLoading();
        EventManager.getSingleton().triggerRequestEvent(event);
    }

    protected final void triggerContentEventProgress(RequestEvent event) {
        ((BaseActivity) getActivity()).showProgress();
        EventManager.getSingleton().triggerRequestEvent(event);
    }

    private String getFragmentTag() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * #### HANDLE EVENT ####
     */
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public final void handleEvent(final ResponseEvent event) {

        // Validate fragment visibility
        if (!isOnTheScreen()) {
            Log.w(TAG, "RECEIVED EVENT IN BACKGROUND WAS DISCARDED: " + event.getType().name());
            return;
        }

        if (event.getSuccess()) {
            Log.i(TAG, "HANDLE EVENT: SUCCESS");
            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
                boolean showContent = onSuccessEvent((ResponseResultEvent<?>) event);
                try {
                    if (showContent) {
                        ((BaseActivity) getActivity()).showContentContainer();
                    }
                    ((BaseActivity) getActivity()).showWarning(event.warning != null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if(getActivity() != null){
                    ((BaseActivity) getActivity()).showWarning(event.warning != null);                    
                } else {
                   return;
                }
                

            }
            handleSuccessEvent(event);

        } else {
            boolean needsErrorHandling = true;
            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
                needsErrorHandling = !onErrorEvent(event);
            }
            if (needsErrorHandling) {
                handleErrorEvent(event);
            }
        }
    }

    /**
     * Handles a successful event and reflects necessary changes on the UI.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     */
    private void handleSuccessEvent(ResponseEvent event) {
        Log.i(TAG, "ON HANDLE SUCCESS EVENT: " + event.getType().toString());
        switch (event.getType()) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
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
    public void handleErrorEvent(final ResponseEvent event) {
        Log.i(TAG, "ON HANDLE ERROR EVENT: " + event.getType());
        if (!(event.request.eventType.equals(EventType.GET_CUSTOMER) && ((BaseActivity) getActivity())
                .getLocalClassName().equals(writeReviewFragment))) {
            if (event.errorCode.isNetworkError()) {
                if (event.type == EventType.GET_SHOPPING_CART_ITEMS_EVENT && null != ((BaseActivity) getActivity())) {
                    ((BaseActivity) getActivity()).updateCartInfo(null);
                }
                if (contentEvents.contains(event.type)) {
                    ((BaseActivity) getActivity()).showError(event.request);
                } else if (userEvents.contains(event.type)) {
                    ((BaseActivity) getActivity()).showContentContainer();
                    
                    // Remove dialog if exist
                    if (dialog != null){
                        try {
                            dialog.dismiss();    
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dialog = DialogGenericFragment.createNoNetworkDialog(getActivity(),
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (null != getActivity()) {
                                    ((BaseActivity) getActivity()).showLoadingInfo();
                                    EventManager.getSingleton().triggerRequestEvent(event.request);
                                    dialog.dismiss();
                                } else {
                                    restartAllFragments();
                                }
                                }
                            }, false);
                    try {
                        dialog.show(getActivity().getSupportFragmentManager(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
                return;
            } else if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                Map<String, ? extends List<String>> messages = event.errorMessages;
                List<String> validateMessages = messages.get(RestConstants.JSON_VALIDATE_TAG);
                String dialogMsg = "";
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = messages.get(RestConstants.JSON_ERROR_TAG);
                }
                if (validateMessages != null) {
                    for (String message : validateMessages) {
                        dialogMsg += message + "\n";
                    }
                } else {
                    for (Entry<String, ? extends List<String>> entry : messages.entrySet()) {
                        dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    }
                }
                if (dialogMsg.equals("")) {
                    dialogMsg = getString(R.string.validation_errortext);
                }
                ((BaseActivity) getActivity()).showContentContainer();

                // Remove dialog if exist
                if (dialog != null){
                    try {
                        dialog.dismiss();    
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                dialog = DialogGenericFragment.newInstance(
                        true, true, false, getString(R.string.validation_title),
                        dialogMsg, getResources().getString(R.string.ok_label), "",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dialog.dismiss();
                                }

                            }

                        });

                
                try {
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                } catch (Exception e) {
                   e.printStackTrace();
                }
                return;
            } else if (event.errorCode == ErrorCode.UNKNOWN_ERROR) {
            dialog = DialogGenericFragment.createServerErrorDialog(getActivity(),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            restartAllFragments();
                            dialog.dismiss();
                        }
                    }, false);

            try {
                dialog.show(((BaseActivity) getActivity()).getSupportFragmentManager(), null);
            } catch (Exception e) {
                Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            }

        } else if (!event.getSuccess()) {

                // Remove dialog if exist
                if (dialog != null){
                    try {
                        dialog.dismiss();    
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                    

                dialog = DialogGenericFragment.createServerErrorDialog(getActivity(),
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ((BaseActivity) getActivity()).showLoadingInfo();

                                EventManager.getSingleton().triggerRequestEvent(event.request);
                                dialog.dismiss();
                            }
                        }, false);
                
                try {
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        else{
            Log.i("TAG","ENTERED HERE");
            ((BaseActivity) getActivity()).showContentContainer();
            return;
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

    @Override
    public final boolean removeAfterHandlingEvent() {
        return false;
    }

    /**
     * Handles a successful event in the concrete activity.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     * @return Returns whether the content container should be shown.
     */
    protected abstract boolean onSuccessEvent(ResponseResultEvent<?> event);

    /**
     * Handles a failed event in the concrete activity. Override this if the concrete activity wants
     * to handle a special error case.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     * @return Whether the concrete activity handled the failed event and no further actions have to
     *         be made.
     */
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
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

    @Override
    public String getMD5Hash() {         
        return md5Hash;
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
    
}
