/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.forms.ShippingMethodFormBuilder;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.GetShippingMethodsHelper;
import pt.rocket.helpers.checkout.SetShippingMethodHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutShippingMethodsFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutShippingMethodsFragment.class);

    private static final String SELECTION_STATE = "selection";

    private static final String SUB_SELECTION_STATE = "sub_selection";
    
    private static CheckoutShippingMethodsFragment shippingMethodsFragment;

    private ViewGroup mShippingMethodsContainer;

    private ShippingMethodFormBuilder mFormResponse;

    private View nFormContainer;

    private int mSelectionSaved = -1;

    private int mSubSelectionSaved = -1;
    
    /**
     * Get instance
     * @return CheckoutShippingMethodsFragment
     */
    public static CheckoutShippingMethodsFragment getInstance(Bundle bundle) {
        shippingMethodsFragment = new CheckoutShippingMethodsFragment();
        return shippingMethodsFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutShippingMethodsFragment() {
        super(EnumSet.of(EventType.GET_SHIPPING_METHODS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout, 
                ConstantsCheckout.CHECKOUT_SHIPPING, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        //setRetainInstance(true);
        // Validate the saved values 
        if(savedInstanceState != null) {
            mSelectionSaved = savedInstanceState.getInt(SELECTION_STATE, -1);
            mSubSelectionSaved = savedInstanceState.getInt(SUB_SELECTION_STATE, -1);
        }
        else
            Log.i(TAG, "SAVED CONTENT VALUES IS NULL");
        TrackerDelegator.trackCheckoutStep(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), R.string.gcheckoutShippingMethods, R.string.xcheckoutshippingmethods, R.string.mixprop_checkout_shipping_methods);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.checkout_shipping_methods, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get containers
        mShippingMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_methods_container);
        // Buttons
        view.findViewById(R.id.checkout_shipping_button_enter).setOnClickListener(this);
        // Get and show addresses
        triggerGetShippingMethods();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mFormResponse == null) 
            return;
        int itemId = mFormResponse.getSelectionId(0);
        if (itemId != -1)
            outState.putInt(SELECTION_STATE, itemId);
        int subItemId = mFormResponse.getSubSelectionId(0, itemId);
        if (itemId != -1 && subItemId != -1)
            outState.putInt(SUB_SELECTION_STATE, subItemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    
    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(ShippingMethodFormBuilder form) {
        Log.i(TAG, "LOAD FORM");
        mFormResponse = form;
        mShippingMethodsContainer.removeAllViews();
        nFormContainer = mFormResponse.generateForm(getBaseActivity());
        mShippingMethodsContainer.addView(nFormContainer);
        mShippingMethodsContainer.refreshDrawableState();
        getBaseActivity().showContentContainer();
        // Set the saved selection
        if(mSelectionSaved != -1)
            mFormResponse.setSelections(0, mSelectionSaved, mSubSelectionSaved);
    }
    
    /**
     * ############# CLICK LISTENER #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_shipping_button_enter) onClickSubmitShippingMethod();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    
    /**
     * Process the click on submit button
     * @author sergiopereira
     */
    private void onClickSubmitShippingMethod() {
        Log.i(TAG, "ON CLICK: SET SHIPPING METHOD");
        ContentValues values = mFormResponse.getValues();
        if(values != null && values.size() > 0){
            JumiaApplication.INSTANCE.setShippingMethod(values);
            triggerSubmitShippingMethod(values);
        }
    }
    
   
    /**
     * ############# RESPONSE #############
     */
  
    /**
     * Process the success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            OrderSummary orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
            // Form
            ShippingMethodFormBuilder form = (ShippingMethodFormBuilder) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            loadForm(form);
            break;
        case SET_SHIPPING_METHOD_EVENT:
            Log.d(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.PAYMENT_METHODS;
            // Switch
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            break;
        }
        
        return true;
    }

    /**
     * Process the error response
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Generic error
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.w(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_SHIPPING_METHOD_EVENT:
            Log.w(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED SET_SHIPPING_METHOD_EVENT");
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger to set the shipping method
     * @param values
     * @author sergiopereira
     */
    private void triggerSubmitShippingMethod(ContentValues values) {
        Log.i(TAG, "TRIGGER: SET SHIPPING METHOD");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetShippingMethodHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetShippingMethodHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the shipping methods
     * @author sergiopereira
     */
    private void triggerGetShippingMethods(){
        Log.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEvent(new GetShippingMethodsHelper(), null, this);
    }
    
    /**
     * ########### RESPONSE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
     
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
}
