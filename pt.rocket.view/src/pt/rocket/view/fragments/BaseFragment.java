package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public abstract class BaseFragment extends Fragment implements ResponseListener,
        OnActivityFragmentInteraction {

    // private static final Set<EventType> HANDLED_EVENTS = EnumSet.of(
    // EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT,
    // EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
    // EventType.GET_SHOPPING_CART_ITEMS_EVENT,
    // EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT,
    // EventType.INITIALIZE,
    // EventType.LOGOUT_EVENT
    // );
    private static final Set<EventType> HANDLED_EVENTS = EnumSet.of(EventType.INITIALIZE,
            EventType.LOGOUT_EVENT);

    private static String writeReviewFragment = "pt.rocket.view.WriteReviewFragmentActivity";

    protected static final String TAG = LogTagHelper.create(BaseFragment.class);

    protected View contentContainer;

    private NavigationAction action;

    private Set<EventType> contentEvents;

    protected DialogFragment dialog;

    private final Set<EventType> allHandledEvents = EnumSet.copyOf(HANDLED_EVENTS);

    private Set<EventType> userEvents;

    public void sendValuesToFragment(int identifier, Object values) {
    }

    public void sendPositionToFragment(int position) {
    }

    public void sendListener(int identifier, OnClickListener clickListener) {
    }

    public boolean allowBackPressed() {
        return true;
    }

    /**
     * Constructor
     */
    public BaseFragment(Set<EventType> contentEvents, Set<EventType> userEvents) {
        this.contentEvents = contentEvents;
        this.userEvents = userEvents;

        this.allHandledEvents.addAll(contentEvents);
        this.allHandledEvents.addAll(userEvents);

    }

    /**
     * #### LIFE CICLE ####
     */
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.getSingleton().addResponseListener(this, allHandledEvents);
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
        // UAirship.shared().getAnalytics().activityStarted(this);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        // UAirship.shared().getAnalytics().activityStopped(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getSingleton().removeResponseListener(this, allHandledEvents);
    }

    /**
     * #### TRIGGER EVENT ####
     */

    /**
     * 
     * @param type
     */
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

    /**
     * 
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("??LowMemPhotoDetails", "LOW MEM");
        ImageLoader.getInstance().clearMemoryCache();
        System.gc();
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
        if (event.getSuccess()) {
            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
                boolean showContent = onSuccessEvent((ResponseResultEvent<?>) event);
                if (showContent) {
                    ((BaseActivity) getActivity()).showContentContainer();
                }
                ((BaseActivity) getActivity()).showWarning(event.warning != null);
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
    @SuppressWarnings("unchecked")
    private void handleSuccessEvent(ResponseEvent event) {
        switch (event.getType()) {
        // case GET_SHOPPING_CART_ITEMS_EVENT:
        // case ADD_ITEM_TO_SHOPPING_CART_EVENT:
        // case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
        // case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
        // ((BaseActivity) getActivity()).updateCartInfo(((ResponseResultEvent<ShoppingCart>)
        // event).result);
        // break;
        case LOGOUT_EVENT:
            getActivity().finish();
            ActivitiesWorkFlow.homePageActivity(getActivity());

            int trackRes;
            if (event.getSuccess()) {
                trackRes = R.string.glogoutsuccess;
            }
            else {
                trackRes = R.string.glogoutfailed;
            }
            AnalyticsGoogle.get().trackAccount(trackRes, null);
            break;
        }
    }

    /**
     * Handles a failed event and shows dialogs to the user.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     */
    private void handleErrorEvent(final ResponseEvent event) {
        Log.i(TAG, "ON HANDLE ERROR EVENT");
        Log.i("ERROR ", " event manager " + event.toString() + " " + event.request.eventType
                + " ACTIVITY " + ((BaseActivity) getActivity()).getLocalClassName());
        if (!(event.request.eventType.equals(EventType.GET_CUSTOMER) && ((BaseActivity) getActivity())
                .getLocalClassName().equals(writeReviewFragment))) {
            if (event.errorCode.isNetworkError()) {
                if (event.type == EventType.GET_SHOPPING_CART_ITEMS_EVENT) {
                    // updateCartInfo(null);
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
}
